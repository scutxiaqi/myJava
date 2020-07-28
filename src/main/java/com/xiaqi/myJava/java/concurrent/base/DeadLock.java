package com.xiaqi.myJava.java.concurrent.base;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 死锁
 * 
 * @author xiaqi
 *
 */
public class DeadLock {
	public static void main(String[] args) {
		Lock lock1 = new ReentrantLock();
		Lock lock2 = new ReentrantLock();
		Thread t1 = new Thread(new Thread1(lock1, lock2));
		Thread t2 = new Thread(new Thread2(lock1, lock2));
		t1.start();
		t2.start();
	}
}

class Thread1 implements Runnable {
	private Lock lock1;
	private Lock lock2;

	public Thread1(Lock lock1, Lock lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}

	@Override
	public void run() {
		lock1.lock();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		lock2.lock();
	}
}

class Thread2 implements Runnable {
	private Lock lock1;
	private Lock lock2;

	public Thread2(Lock lock1, Lock lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}

	@Override
	public void run() {
		lock2.lock();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		lock1.lock();
	}
}
