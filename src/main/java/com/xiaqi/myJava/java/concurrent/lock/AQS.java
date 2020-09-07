package com.xiaqi.myJava.java.concurrent.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;

import sun.misc.Unsafe;

public class AQS {
    /**
     * 当前持有锁的线程.实际上是一个虚节点，本身并不会存储线程信息。
     */
    private transient volatile Node head;

    private transient volatile Node tail;
    /**
     * 同步状态，就是资源<br>
     * state=0 锁可用<br>
     * state=1 锁被占用<br>
     * state>1 锁被占用，值表示同一线程的重入次数
     */
    private volatile int state;
    /**
     * 锁的占有线程
     */
    private transient Thread exclusiveOwnerThread;

    protected final int getState() {
        return state;
    }

    protected final void setState(int newState) {
        state = newState;
    }

    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    /**
     * CAS比较并交换，原子性操作。与预期值一致，返回true。否则false
     * 
     * @param expect 预期值
     * @param update 替换值
     * @return
     */
    protected final boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    /**
     * 尝试获取锁，如果获取失败，则将当前线程包装成节点后加入等待队列
     * 
     * @param arg
     */
    public final void acquire(int arg) {
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }

    /**
     * 将当前调用线程包装成一个【结点】，添加到等待队列尾部
     * 
     * @return 当前线程包装的结点
     */
    private Node addWaiter(Node mode) {
        // 将当前线程包装成节点
        Node node = new Node(Thread.currentThread(), mode);
        Node pred = tail;
        // 先尝试一次添加到尾部，如果添加成功，则不用走下面的enq方法了
        if (pred != null) {
            node.prev = pred;// 该节点的前趋指针指向tail
            if (compareAndSetTail(pred, node)) {// 将尾指针指向该节点
                pred.next = node;// 旧尾部节点的next指针指向该节点.
                return node;
            }
        }
        enq(node);// cas失败,或在pred == null时调用
        return node;
    }

    /**
     * 以自旋的方式不断尝试插入结点至队列尾部
     * 
     * @param node
     * @return 当前结点的前驱结点
     */
    private Node enq(final Node node) {
        for (;;) {// 无限循环,不停的尝试
            Node t = tail;
            if (t == null) {
                if (compareAndSetHead(new Node()))
                    tail = head;// head是一个哨兵的作用,并不代表某个要获取锁的线程节点
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {// 无限循环,直到获取锁.
                final Node p = node.predecessor();// p是当前节点的前驱节点
                // 当前节点的前驱节点是head, 则当前节点为头部节点
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * 判断是否需要阻塞当前线程。<br>
     * ps: 对于在等待队列中的线程，如果要阻塞它，需要确保将来有线程可以唤醒它，AQS中通过将前驱结点的状态置为SIGNAL:-1来表示将来会唤醒当前线程，当前线程可以安心的阻塞。
     * 
     * @param pred
     * @param node
     * @return true表示需要阻塞当前线程
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL) // 这个状态说明当前节点的前驱将来会唤醒我，我可以安心的阻塞
            return true;
        if (ws > 0) { // CANCELLED=1 : 取消。表示后驱结点被中断或超时，需要移出队列
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    /**
     * 禁止当前线程进行线程调度
     * 
     * @return
     */
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);// 禁止当前线程进行线程调度. 类似于wait()方法
        return Thread.interrupted(); // 测试当前线程是否已经中断
    }

    private void cancelAcquire(Node node) {

    }

    /**
     * 尝试获取锁，成功返回true，失败返回false
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 中断当前线程
     */
    static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    public final boolean release(int arg) {
        if (tryRelease(arg)) {// 尝试释放锁
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }

    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 唤醒当前节点的后继节点
     * 
     * @param node 当前节点
     */
    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);
    }

    /**
     * 判断队列中是否有节点
     * 
     * @return
     */
    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
    }

    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
    /**
     * 尝试释放共享锁
     */
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }
    /**
     * 共享模式下的释放动作
     */
    private void doReleaseShared() {
        for (;;) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue; // loop to recheck cases
                    unparkSuccessor(h);
                } else if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue; // loop on failed CAS
            }
            if (h == head) // loop if head changed
                break;
        }
    }

    public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }

    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.SHARED); // node代表当前线程包装的节点
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();// 获取前节点
                if (p == head) {
                    int r = tryAcquireShared(arg); // 尝试获取锁
                    if (r >= 0) {// r >= 0表示获取成功
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * 
     * @param node
     * @param propagate
     */
    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        setHead(node); // 将当前节点设置为头节点
        // 判断是否需要唤醒后继节点
        if (propagate > 0 || h == null || h.waitStatus < 0 || (h = head) == null || h.waitStatus < 0) {
            Node s = node.next;
            if (s == null || s.isShared())
                doReleaseShared();
        }
    }

    static final class Node {
        static final Node SHARED = new Node(); // 是否是共享模式
        static final Node EXCLUSIVE = null; // 排他模式

        static final int CANCELLED = 1; // 取消状态
        static final int SIGNAL = -1; // 表示后续结点被阻塞了（当前结点在入队后、阻塞前，应确保将其prev结点类型改为SIGNAL，以便prev结点取消或释放时将当前结点唤醒。）
        static final int CONDITION = -2; // 等待唤醒条件
        static final int PROPAGATE = -3; // 节点状态需要向后传播
        volatile Node prev;
        /**
         * 后继节点
         */
        volatile Node next;
        /**
         * 节点对应的线程
         */
        volatile Thread thread;
        /**
         * 等待队列中的后继节点
         */
        Node nextWaiter;
        /**
         * INITAL： 0 - 新结点会处于这种状态。<br>
         * CANCELLED： 1 - 取消，表示后续结点被中断或超时，需要移出队列； <br>
         * SIGNAL： -1 - 发信号，表示后续结点被阻塞了（当前结点在入队后、阻塞前，应确保将其prev结点类型改为SIGNAL，以便prev结点取消或释放时将当前结点唤醒。）<br>
         * CONDITION： -2 Condition专用，表示当前结点在Condition队列中，因为等待某个条件而被阻塞了；<br>
         * PROPAGATE： -3- 传播，适用于共享模式。（比如连续的读操作结点可以依次进入临界区，设为PROPAGATE有助于实现这种迭代操作。）
         * 
         * waitStatus表示的是后续结点状态，这是因为AQS中使用CLH队列实现线程的结构管理，而CLH结构正是用前一结点某一属性表示当前结点的状态，这样更容易实现取消和超时功能。
         */
        volatile int waitStatus;

        Node() {
        }

        Node(Thread thread, Node mode) { // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * 返回前驱节点
         * 
         * @return
         * @throws NullPointerException
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }
    }

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
}
