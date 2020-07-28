package com.xiaqi.myJava.java.concurrent.lock;

public class MyReentrantLock {
	private final Sync sync;

	public MyReentrantLock() {
		sync = new NonfairSync();
	}

	public void lock() {
		sync.lock();
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
