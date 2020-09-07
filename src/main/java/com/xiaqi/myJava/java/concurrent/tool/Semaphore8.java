package com.xiaqi.myJava.java.concurrent.tool;

import com.xiaqi.myJava.java.concurrent.lock.AQS;

public class Semaphore8 {
    private final Sync sync;
    
    public Semaphore8(int permits) {
        sync = new NonfairSync(permits);
    }
    
    public void acquire() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }
    
    

    abstract static class Sync extends AQS {
        Sync(int permits) {
            setState(permits);
        }

        final int nonfairTryAcquireShared(int acquires) {
            for (;;) {
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining))
                    return remaining;
            }
        }
    }

    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = -2694183684443567898L;

        NonfairSync(int permits) {
            super(permits);
        }

        protected int tryAcquireShared(int acquires) {
            return nonfairTryAcquireShared(acquires);
        }
    }

}
