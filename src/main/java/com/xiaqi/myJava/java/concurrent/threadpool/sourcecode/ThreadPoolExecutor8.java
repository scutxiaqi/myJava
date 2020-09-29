package com.xiaqi.myJava.java.concurrent.threadpool.sourcecode;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor8 extends AbstractExecutorService {
    private final BlockingQueue<Runnable> workQueue;
    
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * 工作线程集合.
     */
    private final HashSet<Worker> workers = new HashSet<Worker>();
    
    private int largestPoolSize;

    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;

    private volatile int corePoolSize;
    private volatile int maximumPoolSize;

    /**
     * 保存线程池状态和工作线程数: 低29位: 工作线程数 | 高3位 : 线程池状态
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    // 最大线程数: 2^29-1
    private static final int CAPACITY = (1 << COUNT_BITS) - 1; // 00011111 11111111 11111111 11111111
    // 线程池状态
    private static final int RUNNING = -1 << COUNT_BITS; // 11100000 00000000 00000000 00000000
    private static final int SHUTDOWN = 0 << COUNT_BITS; // 00000000 00000000 00000000 00000000
    private static final int STOP = 1 << COUNT_BITS; // 00100000 00000000 00000000 00000000
    private static final int TIDYING = 2 << COUNT_BITS; // 01000000 00000000 00000000 00000000
    private static final int TERMINATED = 3 << COUNT_BITS; // 01100000 00000000 00000000 00000000

    /**
     * 获取线程池状态
     * 
     * @param c
     * @return 线程池状态
     */
    private static int runStateOf(int c) {
        return c & ~CAPACITY;
    }

    /**
     * 获取工作线程数
     * 
     * @param c
     * @return 工作线程数
     */
    private static int workerCountOf(int c) {
        return c & CAPACITY;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }
    
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * 使用给定的参数创建ThreadPoolExecutor.
     *
     * @param corePoolSize    核心线程池中的最大线程数
     * @param maximumPoolSize 总线程池中的最大线程数
     * @param keepAliveTime   空闲线程的存活时间
     * @param unit            keepAliveTime的单位
     * @param workQueue       任务队列, 保存已经提交但尚未被执行的线程
     * @param threadFactory   线程工厂(用于指定如果创建一个线程)
     * @param handler         拒绝策略 (当任务太多导致工作队列满时的处理策略)
     */
    public ThreadPoolExecutor8(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime); // 使用纳秒保存存活时间
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    @Override
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();

        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) { // CASE1: 工作线程数 < 核心线程池上限
            if (addWorker(command, true)) // 添加工作线程并执行
                return;
            c = ctl.get();
        }

        // 执行到此处, 说明工作线程创建失败 或 工作线程数 ≥ corePoolSize
        if (isRunning(c) && workQueue.offer(command)) { // CASE2: 插入任务至队列

            // 再次检查线程池状态
            int recheck = ctl.get();
            if (!isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        } else if (!addWorker(command, false)) // CASE3: 插入队列失败, 判断工作线程数 < 总线程池上限
            reject(command); // 执行拒绝策略
    }

    /**
     * 添加工作线程并执行任务
     *
     * @param firstTask 如果指定了该参数, 表示将立即创建一个新工作线程执行该firstTask任务; 否则复用已有的工作线程，从工作队列中获取任务并执行
     * @param core      执行任务的工作线程归属于哪个线程池: true-核心线程池 false-非核心线程池
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry: for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c); // 获取线程池状态

            /**
             * 这个if主要是判断哪些情况下, 线程池不再接受新任务执行, 而是直接返回.总结下, 有以下几种情况：<br>
             * 1. 线程池状态为 STOP 或 TIDYING 或 TERMINATED: 线程池状态为上述任一一种时, 都不会再接受任务，所以直接返回<br>
             * 2. 线程池状态≥ SHUTDOWN 且 firstTask != null: 因为当线程池状态≥ SHUTDOWN时, 不再接受新任务的提交，所以直接返回<br>
             * 3. 线程池状态≥ SHUTDOWN 且 队列为空: 队列中已经没有任务了, 所以也就不需要执行任何任务了，可以直接返回
             */
            if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c); // 获取工作线程数

                /**
                 * 这个if主要是判断工作线程数是否超限, 以下任一情况属于属于超限, 直接返回:<br>
                 * 1. 工作线程数超过最大工作线程数(2^29-1)<br>
                 * 2. 工作线程数超过核心线程池上限(入参core为true, 表示归属核心线程池)<br>
                 * 3. 工作线程数超过总线程池上限(入参core为false, 表示归属非核心线程池)
                 */
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;

                if (compareAndIncrementWorkerCount(c)) // 工作线程数加1
                    break retry; // 跳出最外层循环

                c = ctl.get();
                if (runStateOf(c) != rs) // 线程池状态发生变化, 重新自旋判断
                    continue retry;
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask); // 将任务包装成工作线程
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // 重新检查线程池状态
                    int rs = runStateOf(ctl.get());
                    if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive())
                            throw new IllegalThreadStateException();
                        workers.add(w); // 加入工作线程集合
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (!workerStarted) // 创建/启动工作线程失败, 需要执行回滚操作
                addWorkerFailed(w);
        }
        return workerStarted;
    }
    
    private void addWorkerFailed(Worker w) {
        
    }

    public boolean remove(Runnable task) {
        return true;
    }

    final void reject(Runnable command) {
        // handler.rejectedExecution(command, this);
    }

    /**
     * Worker表示线程池中的一个工作线程, 可以与任务相关联. 由于实现了AQS框架, 其同步状态值的定义如下: -1: 初始状态 0: 无锁状态 1: 加锁状态
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        private static final long serialVersionUID = -22541864613827810L;
        final Thread thread;
        Runnable firstTask;

        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            
        }
    }
    
    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Runnable> shutdownNow() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isShutdown() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTerminated() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }
}
