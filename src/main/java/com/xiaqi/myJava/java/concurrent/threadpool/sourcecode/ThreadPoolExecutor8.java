package com.xiaqi.myJava.java.concurrent.threadpool.sourcecode;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
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
    /**
     * 该变量记录了线程池在整个生命周期中曾经出现的最大工作线程数
     */
    private int largestPoolSize;

    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut;
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
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

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        do {
        } while (!compareAndDecrementWorkerCount(ctl.get()));
    }

    public ThreadPoolExecutor8(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultHandler);
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

    /**
     * 执行任务。此方法是线程池核心方法，步骤如下： <br>
     * 1. 工作线程数 < 核心线程数 <br>
     * 2. 插入任务至队列 <br>
     * 3. 插入队列失败（队列满了）, 判断工作线程数 < 总线程池 <br>
     * 4.
     */
    @Override
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();

        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) { // CASE1: 工作线程数 < 核心线程数
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
     * 添加工作线程并执行任务。步骤如下： <br>
     * 1. 自旋操作，主要是对线程池的状态进行一些判断，如果状态不适合接受新任务，或者工作线程数超出了限制，则直接返回false。 <br>
     * 2. 创建工作线程并执行任务
     * 
     * @param firstTask 如果指定了该参数, 表示将立即创建一个新工作线程执行该firstTask任务; 否则复用已有的工作线程，从工作队列中获取任务并执行
     * @param core      执行任务的工作线程归属于哪个线程池: true-核心线程池 false-非核心线程池
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        // 步骤1
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
        // 步骤2
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
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        public void run() {
            runWorker(this);
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }
    }

    /**
     * 用于执行任务, 整体流程如下：<br>
     * 1. while循环不断地通过getTask()方法从队列中获取任务（如果工作线程自身携带着任务，则执行携带的任务）；<br>
     * 2. 控制执行线程的中断状态，保证如果线程池正在停止，则线程必须是中断状态，否则线程必须不是中断状态；<br>
     * 3. 调用task.run()执行任务；<br>
     * 4. 处理工作线程的退出工作。
     * 
     * @param w
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread(); // 执行任务的线程
        Runnable task = w.firstTask; // 任务, 如果是null则从队列取任务
        w.firstTask = null;
        w.unlock(); // 允许执行线程被中断
        boolean completedAbruptly = true; // 表示是否因为中断而导致退出
        try {
            while (task != null || (task = getTask()) != null) { // 当task==null时会通过getTask从队列取任务
                w.lock();

                /**
                 * 下面这个if判断的作用如下:<br>
                 * 1.保证当线程池状态为STOP/TIDYING/TERMINATED时，当前执行任务的线程wt是中断状态(因为线程池处于上述任一状态时，均不能再执行新任务)
                 * 2.保证当线程池状态为RUNNING/SHUTDOWN时，当前执行任务的线程wt不是中断状态
                 */
                if ((runStateAtLeast(ctl.get(), STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted())
                    wt.interrupt();

                try {
                    beforeExecute(wt, task); // 钩子方法，由子类自定义实现
                    Throwable thrown = null;
                    try {
                        task.run(); // 执行任务
                    } catch (RuntimeException x) {
                        thrown = x;
                        throw x;
                    } catch (Error x) {
                        thrown = x;
                        throw x;
                    } catch (Throwable x) {
                        thrown = x;
                        throw new Error(x);
                    } finally {
                        afterExecute(task, thrown); // 钩子方法，由子类自定义实现
                    }
                } finally {
                    task = null;
                    w.completedTasks++; // 完成任务数+1
                    w.unlock();
                }
            }

            // 执行到此处, 说明该工作线程自身既没有携带任务, 也没从任务队列中获取到任务
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly); // 处理工作线程的退出工作
        }
    }

    /**
     * 从阻塞队列中获取一个任务，如果获取失败则返回null。通过自旋，不断地尝试
     * 
     * @return
     */
    private Runnable getTask() {
        boolean timedOut = false; // 表示上次从阻塞队列中取任务时是否超时
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c); // 获取线程池状态
            /**
             * 以下IF用于判断哪些情况下不允许再从队列获取任务:<br>
             * 1. 线程池进入停止状态（STOP/TIDYING/TERMINATED）, 此时即使队列中还有任务未执行, 也不再执行<br>
             * 2. 线程池非RUNNING状态, 且队列为空
             */
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount(); // 工作线程数减1
                return null;
            }
            int wc = workerCountOf(c); // 获取工作线程数
            /**
             * timed变量用于判断是否需要进行超时控制:<br>
             * 对于核心线程池中的工作线程, 除非设置了allowCoreThreadTimeOut==true, 否则不会超时回收;<br>
             * 对于非核心线程池中的工作线程, 都需要超时控制
             */
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
            // 这里主要是当外部通过setMaximumPoolSize方法重新设置了最大线程数时,需要回收多出的工作线程
            if ((wc > maximumPoolSize || (timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))
                    return null;
                continue;
            }
            try {
                Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
                if (r != null)
                    return r;
                timedOut = true; // 超时仍未获取到任务
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {

    }

    protected void beforeExecute(Thread t, Runnable r) {
    }

    protected void afterExecute(Runnable r, Throwable t) {
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return runStateAtLeast(ctl.get(), TIDYING) ? 0 : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
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
