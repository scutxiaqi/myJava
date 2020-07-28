package com.xiaqi.myJava.java.concurrent.base;

/**
 * 最简单的wait、notify用法。用于线程间的通信
 * 
 * @author xiaqi
 *
 */
public class WaitNotify {
	public static void main(String[] args) throws Exception {
		Object lock = new Object();
		Thread11 thread1 = new Thread11(lock);
		thread1.start();
		Thread.sleep(1000);
		Thread22 thread2 = new Thread22(lock);
		thread2.start();
	}
}

class Thread11 extends Thread {
	private Object lock;

	public Thread11(Object lock) {
		this.lock = lock;
	}

	public void run() {
		try {
			synchronized (lock) {
				System.out.println("开始------wait");
				lock.wait();
				System.out.println("结束------wait");
			}
		} catch (InterruptedException e) {
		}
	}
}

class Thread22 extends Thread {
	private Object lock;

	public Thread22(Object lock) {
		this.lock = lock;
	}

	public void run() {
		try {
			synchronized (lock) {
				lock.notify();
				System.out.println("完成------notify");
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
		}
	}
}
