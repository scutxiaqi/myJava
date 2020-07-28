package com.xiaqi.myJava.java.concurrent.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁：等待时间最长的先获取锁
 *
 */
public class FairLook {
	public static void main(String[] args) throws Exception {
		MyService service = new MyService();
		Thread[] array = new Thread[10];
		for (int i = 0; i < 10; i++) {
			array[i] = new MyThread(service);
		}
		for (int i = 0; i < 10; i++) {
			array[i].start();
			Thread.sleep(100);
		}
	}
}

class MyService {
	private ReentrantLock lock = new ReentrantLock(true); // 创建一个公平锁。默认非公平

	public void service() {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获得lock");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}
}

class MyThread extends Thread {
	MyService service;

	public MyThread(MyService service) {
		super();
		// this.setName(name);
		this.service = service;
	}

	@Override
	public void run() {
		service.service();
	}
}
