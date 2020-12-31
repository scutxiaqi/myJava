package com.xiaqi.myJava.java.concurrent.threadpool.sourcecode;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fork/Join线程池，在JDK1.7时引入，实现Fork/Join框架的核心类。
 */
public class ForkJoinPool8 {
//  低位和高位掩码
    private static final long SP_MASK = 0xffffffffL;
    private static final long UC_MASK = ~SP_MASK;

// 活跃线程数
    private static final int AC_SHIFT = 48;
    private static final long AC_UNIT = 0x0001L << AC_SHIFT; // 活跃线程数增量
    private static final long AC_MASK = 0xffffL << AC_SHIFT; // 活跃线程数掩码

// 工作线程数
    private static final int TC_SHIFT = 32;
    private static final long TC_UNIT = 0x0001L << TC_SHIFT; // 工作线程数增量
    private static final long TC_MASK = 0xffffL << TC_SHIFT; // 掩码
    private static final long ADD_WORKER = 0x0001L << (TC_SHIFT + 15); // 创建工作线程标志

// 池状态
    private static final int RSLOCK = 1;
    private static final int RSIGNAL = 1 << 1;
    private static final int STARTED = 1 << 2;
    private static final int STOP = 1 << 29;
    private static final int TERMINATED = 1 << 30;
    private static final int SHUTDOWN = 1 << 31;

// 实例字段
    /**
     * ForkJoinPool 的内部状态都是通过一个64位的 long 型 变量ctl来存储，它由四个16位的子域组成：<br>
     * AC：正在运行工作线程数减去目标并行度，高16位 <br>
     * TC：总工作线程数减去目标并行度，中高16位 <br>
     * SS：栈顶等待线程的版本计数和状态，中低16位 <br>
     * ID： 栈顶 WorkQueue 在池中的索引（poolIndex），低16位
     */
    volatile long ctl; // 主控制参数
    volatile int runState; // 运行状态锁
    final int config; // 并行度|模式
    int indexSeed; // 用于生成工作线程索引
    volatile WorkQueue[] workQueues; // 主对象注册信息，workQueue
    final ForkJoinWorkerThreadFactory factory;// 线程工厂
    final UncaughtExceptionHandler ueh; // 每个工作线程的异常信息
    final String workerNamePrefix; // 用于创建工作线程的名称
    volatile AtomicLong stealCounter; // 偷取任务总数，也可作为同步监视器

    /** 静态初始化字段 */
//线程工厂
    public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
//启动或杀死线程的方法调用者的权限
    private static final RuntimePermission modifyThreadPermission;
// 公共静态pool
    static final ForkJoinPool8 common;
//并行度，对应内部common池
    static final int commonParallelism;
//备用线程数，在tryCompensate中使用
    private static int commonMaxSpares;
//创建workerNamePrefix(工作线程名称前缀)时的序号
    private static int poolNumberSequence;
//线程阻塞等待新的任务的超时值(以纳秒为单位)，默认2秒
    private static final long IDLE_TIMEOUT = 2000L * 1000L * 1000L; // 2sec
//空闲超时时间，防止timer未命中
    private static final long TIMEOUT_SLOP = 20L * 1000L * 1000L; // 20ms
//默认备用线程数
    private static final int DEFAULT_COMMON_MAX_SPARES = 256;
//阻塞前自旋的次数，用在在awaitRunStateLock和awaitWork中
    private static final int SPINS = 0;
//indexSeed的增量
    private static final int SEED_INCREMENT = 0x9e3779b9;

    /**
     * @param parallelism      并行度，默认为CPU数，最小为1
     * @param factory          工作线程工厂
     * @param handler          处理工作线程运行任务时的异常处理器，默认为null
     * @param mode             调度模式
     * @param workerNamePrefix 工作线程的名称前缀
     */
    private ForkJoinPool8(int parallelism, ForkJoinWorkerThreadFactory factory, UncaughtExceptionHandler handler, int mode, String workerNamePrefix) {
        this.workerNamePrefix = workerNamePrefix;
        this.factory = factory;
        this.ueh = handler;
        this.config = (parallelism & SMASK) | mode;
        long np = (long) (-parallelism); // offset ctl counts
        this.ctl = ((np << AC_SHIFT) & AC_MASK) | ((np << TC_SHIFT) & TC_MASK);
    }

    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) {
        if (task == null)
            throw new NullPointerException();
        externalPush(task);
        return task;
    }

    /**
     * 尝试将给定的任务添加到提交者的当前队列（其中一个submission queue). <br>
     * 1.根据线程随机变量、任务队列数组信息，计算命中槽（即本次提交的任务应该添加到任务队列数组中的哪个队列），如果命中且队列中任务数<1，则创建或激活一个工作线程；<br>
     * 2.否则，调用externalSubmit初始化队列，并入队
     **/
    final void externalPush(ForkJoinTask<?> task) {
        WorkQueue[] ws;
        WorkQueue q;
        int m;
        int r = 0;// ThreadLocalRandom.getProbe(); //探针值，用于计算WorkQueue槽位索引
        int rs = runState; // runState运行状态，初始化为0
        // workQueues为整个线程池的工作队列（其实就是一个数组）
        // 当工作队列不为空，长度至少为1（m表示ws工作队列数组当前能够表示的最大下标）
        // 其中m&r表示随机数不大于m，然后&SQMASK（SQMASK = 0x007e;）相当于只取偶数，并且偶数不大于0x7e(126),这里从随机的偶数槽位取出WorkQueue不为空，
        // r!=0, 说明不是第一个
        // rs>0 说明运行状态被初始化过
        // 锁定workQueue
        if ((ws = workQueues) != null && (m = (ws.length - 1)) >= 0 && (q = ws[m & r & SQMASK]) != null && r != 0 && rs > 0 && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
            ForkJoinTask<?>[] a;
            int am, n, s;
            if ((a = q.array) != null && // WorkQueue中的任务数组不为空
                    (am = a.length - 1) > (n = (s = q.top) - q.base)) { // 数组未装满？
                int j = ((am & s) << ASHIFT) + ABASE; //计算任务索引位置
                U.putOrderedObject(a, j, task); // 放入任务
                U.putOrderedInt(q, QTOP, s + 1); // top+1
                U.putIntVolatile(q, QLOCK, 0); // 释放任务队列(WorkQueue)的锁
                if (n <= 1) 
                    signalWork(ws, q);// 任务数小于1时尝试创建或激活一个工作线程
                return;
            }
            U.compareAndSwapInt(q, QLOCK, 1, 0); // 释放锁
        }
        // 初始化workQueues及相关属性
        externalSubmit(task);
    }

    /**
     * 该方法的是一个完整的外部提交任务入任务队列的逻辑
     * 
     * @param task
     */
    private void externalSubmit(ForkJoinTask<?> task) {
        int r = 0; // 初始化调用线程的探针值，用于计算WorkQueue索引
        // if ((r = ThreadLocalRandom.getProbe()) == 0) {
        // ThreadLocalRandom.localInit();
        // r = ThreadLocalRandom.getProbe();
        // }
        for (;;) {
            WorkQueue[] ws;
            WorkQueue q;
            int rs, m, k;
            boolean move = false;
            if ((rs = runState) < 0) { // 如果池为终止状态(runState<0)，调用tryTerminate来终止线程池，并抛出任务拒绝异常
                tryTerminate(false, false); // help terminate
                throw new RejectedExecutionException();
            } else if ((rs & STARTED) == 0 || ((ws = workQueues) == null || (m = ws.length - 1) < 0)) { // 如果workQueues未初始化
                int ns = 0;
                rs = lockRunState();// 锁定runState
                try {
                    if ((rs & STARTED) == 0) { // 再判断一次状态是否为初始化，因为在lockRunState过程中有可能状态被别的线程更改了
                        U.compareAndSwapObject(this, STEALCOUNTER, null, new AtomicLong());// 初始化stealCounter
                        // 创建workQueues，容量为2的幂次方
                        int p = config & SMASK; // ensure at least 2 slots
                        int n = (p > 1) ? p - 1 : 1;
                        n |= n >>> 1;
                        n |= n >>> 2;
                        n |= n >>> 4;
                        n |= n >>> 8;
                        n |= n >>> 16;
                        n = (n + 1) << 1;
                        workQueues = new WorkQueue[n];
                        ns = STARTED;
                    }
                } finally {
                    unlockRunState(rs, (rs & ~RSLOCK) | ns);// 解锁并更新runState
                }
            } else if ((q = ws[k = r & m & SQMASK]) != null) {// 获取随机偶数槽位的workQueue
                if (q.qlock == 0 && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                    ForkJoinTask<?>[] a = q.array;
                    int s = q.top;
                    boolean submitted = false; // initial submission or resizing
                    try { // locked version of push
                        if ((a != null && a.length > s + 1 - q.base) || (a = q.growArray()) != null) {
                            int j = (((a.length - 1) & s) << ASHIFT) + ABASE;
                            U.putOrderedObject(a, j, task);
                            U.putOrderedInt(q, QTOP, s + 1);
                            submitted = true;
                        }
                    } finally {
                        U.compareAndSwapInt(q, QLOCK, 1, 0);
                    }
                    if (submitted) {
                        signalWork(ws, q);
                        return;
                    }
                }
                move = true; // move on failure
            } else if (((rs = runState) & RSLOCK) == 0) { // create new queue
                q = new WorkQueue(this, null);
                q.hint = r;
                q.config = k | SHARED_QUEUE;
                q.scanState = INACTIVE;
                rs = lockRunState(); // publish index
                if (rs > 0 && (ws = workQueues) != null && k < ws.length && ws[k] == null)
                    ws[k] = q; // else terminated
                unlockRunState(rs, rs & ~RSLOCK);
            } else
                move = true; // move if busy
            if (move) {
                // r = ThreadLocalRandom.advanceProbe(r);
            }
        }
    }

    private boolean tryTerminate(boolean now, boolean enable) {
        return false;
    }

    private int lockRunState() {
        return 0;
    }

    private void unlockRunState(int oldRunState, int newRunState) {

    }

    final void signalWork(WorkQueue[] ws, WorkQueue q) {

    }

    public static ForkJoinPool8 commonPool() {
        // assert common != null : "static init error";
        return common;
    }

    private static ForkJoinPool8 makeCommonPool() {
        int parallelism = -1;
        ForkJoinWorkerThreadFactory factory = null;
        UncaughtExceptionHandler handler = null;
        try { // ignore exceptions in accessing/parsing properties
            String pp = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
            String fp = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
            String hp = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
            if (pp != null)
                parallelism = Integer.parseInt(pp);
            if (fp != null)
                factory = ((ForkJoinWorkerThreadFactory) ClassLoader.getSystemClassLoader().loadClass(fp).newInstance());
            if (hp != null)
                handler = ((UncaughtExceptionHandler) ClassLoader.getSystemClassLoader().loadClass(hp).newInstance());
        } catch (Exception ignore) {
        }
        if (factory == null) {
            if (System.getSecurityManager() == null)
                factory = defaultForkJoinWorkerThreadFactory;
            else // use security-managed default
                factory = new InnocuousForkJoinWorkerThreadFactory();
        }
        if (parallelism < 0 && // default 1 less than #cores
                (parallelism = Runtime.getRuntime().availableProcessors() - 1) <= 0)
            parallelism = 1;
        if (parallelism > MAX_CAP)
            parallelism = MAX_CAP;
        return new ForkJoinPool8(parallelism, factory, handler, LIFO_QUEUE, "ForkJoinPool.commonPool-worker-");
    }

    @sun.misc.Contended
    static final class WorkQueue {
        // 初始队列容量，2的幂
        static final int INITIAL_QUEUE_CAPACITY = 1 << 13;
        // 最大队列容量
        static final int MAXIMUM_QUEUE_CAPACITY = 1 << 26; // 64M

        // 实例字段
        volatile int scanState; // Woker状态, <0: inactive; odd:scanning
        int stackPred; // 记录前一个栈顶的ctl
        int nsteals; // 偷取任务数
        int hint; // 记录偷取者索引，初始为随机索引
        int config; // 池索引和模式
        volatile int qlock; // 1: locked, < 0: terminate; else 0
        volatile int base; // 下一个poll操作的索引（栈底/队列头）
        int top; // 下一个push操作的索引（栈顶/队列尾）
        ForkJoinTask<?>[] array; // 任务数组
        final ForkJoinPool8 pool; // the containing pool (may be null)
        final ForkJoinWorkerThread owner; // 当前工作队列的工作线程，共享模式下为null
        volatile Thread parker; // 调用park阻塞期间为owner，其他情况为null
        volatile ForkJoinTask<?> currentJoin; // 记录被join过来的任务
        volatile ForkJoinTask<?> currentSteal; // 记录从其他工作队列偷取过来的任务

        WorkQueue(ForkJoinPool8 pool, ForkJoinWorkerThread owner) {
            this.pool = pool;
            this.owner = owner;
            // Place indices in the center of array (that is not yet allocated)
            base = top = INITIAL_QUEUE_CAPACITY >>> 1;
        }

        final ForkJoinTask<?>[] growArray() {
            return null;
        }
    }

    public static interface ForkJoinWorkerThreadFactory {
        public ForkJoinWorkerThread newThread(ForkJoinPool pool);
    }

    static final class DefaultForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return null;
        }
    }

    static final class InnocuousForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    // Constants shared across ForkJoinPool and WorkQueue

    // 限定参数
    static final int SMASK = 0xffff; // 低位掩码，也是最大索引位
    static final int MAX_CAP = 0x7fff; // 工作线程最大容量
    static final int EVENMASK = 0xfffe; // 偶数低位掩码
    static final int SQMASK = 0x007e; // workQueues 数组最多64个槽位

    // ctl 子域和 WorkQueue.scanState 的掩码和标志位
    static final int SCANNING = 1; // 标记是否正在运行任务
    static final int INACTIVE = 1 << 31; // 失活状态 负数
    static final int SS_SEQ = 1 << 16; // 版本戳，防止ABA问题

    // ForkJoinPool.config 和 WorkQueue.config 的配置信息标记
    static final int MODE_MASK = 0xffff << 16; // 模式掩码
    static final int LIFO_QUEUE = 0; // LIFO队列
    static final int FIFO_QUEUE = 1 << 16;// FIFO队列
    static final int SHARED_QUEUE = 1 << 31; // 共享模式队列，负数

    // Unsafe mechanics
    private static final sun.misc.Unsafe U;
    private static final int ABASE;
    private static final int ASHIFT;
    private static final long CTL;
    private static final long RUNSTATE;
    private static final long STEALCOUNTER;
    private static final long PARKBLOCKER;
    private static final long QTOP;
    private static final long QLOCK;
    private static final long QSCANSTATE;
    private static final long QPARKER;
    private static final long QCURRENTSTEAL;
    private static final long QCURRENTJOIN;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ForkJoinPool.class;
            CTL = U.objectFieldOffset(k.getDeclaredField("ctl"));
            RUNSTATE = U.objectFieldOffset(k.getDeclaredField("runState"));
            STEALCOUNTER = U.objectFieldOffset(k.getDeclaredField("stealCounter"));
            Class<?> tk = Thread.class;
            PARKBLOCKER = U.objectFieldOffset(tk.getDeclaredField("parkBlocker"));
            Class<?> wk = WorkQueue.class;
            QTOP = U.objectFieldOffset(wk.getDeclaredField("top"));
            QLOCK = U.objectFieldOffset(wk.getDeclaredField("qlock"));
            QSCANSTATE = U.objectFieldOffset(wk.getDeclaredField("scanState"));
            QPARKER = U.objectFieldOffset(wk.getDeclaredField("parker"));
            QCURRENTSTEAL = U.objectFieldOffset(wk.getDeclaredField("currentSteal"));
            QCURRENTJOIN = U.objectFieldOffset(wk.getDeclaredField("currentJoin"));
            Class<?> ak = ForkJoinTask[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
        commonMaxSpares = DEFAULT_COMMON_MAX_SPARES;
        defaultForkJoinWorkerThreadFactory = new DefaultForkJoinWorkerThreadFactory();
        modifyThreadPermission = new RuntimePermission("modifyThread");

        common = java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<ForkJoinPool8>() {
            public ForkJoinPool8 run() {
                return makeCommonPool();
            }
        });
        int par = common.config & SMASK; // report 1 even if threads disabled
        commonParallelism = par > 0 ? par : 1;
    }
}
