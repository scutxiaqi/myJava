package com.xiaqi.myJava.java.concurrent.tool;

import com.xiaqi.myJava.java.concurrent.lock.AQS;

public class CountDownLatch8 {
    private final Sync sync;

    public CountDownLatch8(int count) {
        if (count < 0)
            throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    /**
     * 将计数值减1。如果计数值为0，释放所有等待线程
     */
    public void countDown() {
        sync.releaseShared(1);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    private static final class Sync extends AQS {
        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        /**
         * 尝试获取共享锁
         */
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        /**
         * 尝试释放共享锁。这是一个钩子方法，当state值首次变为0时，返回true
         */
        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c - 1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }
}
