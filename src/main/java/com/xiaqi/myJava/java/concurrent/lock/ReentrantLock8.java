package com.xiaqi.myJava.java.concurrent.lock;

public class ReentrantLock8 {
    private final Sync sync;

    public ReentrantLock8() {
        sync = new NonfairSync();
    }

    public void lock() {
        sync.lock();
    }

    static final class FairSync extends Sync {
        final void lock() {
            acquire(1);
        }

        /**
         * 尝试获取锁，成功返回true，失败返回false
         */
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState(); // 同步状态
            if (c == 0) { // 0表示锁未被占用
                if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                    // 更新state成功，设置锁的占有线程为当前线程
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {// 如果当前线程为锁占有线程
                int nextc = c + acquires; //重入时，同步状态累加
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());// 设置当前线程独占资源
            else {
                acquire(1);
            }
        }

        /**
         * 尝试获取锁，成功返回true，失败返回false
         */
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }

    }

    abstract static class Sync extends AQS {
        abstract void lock();

        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {// 如果该线程已经拿到了锁，此处为可重入锁的实现方式
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc); // state加1
                return true;
            }
            return false;
        }
    }
}
