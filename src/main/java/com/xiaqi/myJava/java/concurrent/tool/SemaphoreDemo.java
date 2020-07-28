package com.xiaqi.myJava.java.concurrent.tool;

import java.util.concurrent.Semaphore;

/**
 * Semaphore作用是控制同时访问临界资源的线程个数。<br/>
 * Semaphore允许多个线程同时访问临界资源，而synchronized/Lock只允许一个线程。
 * 场景：主要用来做流量控制。例如：有10个人在银行办理业务，只有2个工作窗口
 *
 */
public class SemaphoreDemo {
	public static void main(String args[]) {
		for (int i = 0; i < 10; i++) {
			MyThread t = new MyThread("thread" + (i + 1), new MyService());
			t.start();
		}
	}
}

class MyThread extends Thread {
	MyService service;

	public MyThread(String name, MyService service) {
		super();
		this.setName(name);
		this.service = service;
	}

	@Override
	public void run() {
		service.doSomething();
	}
}

class MyService {
	private Semaphore semaphore = new Semaphore(5);// 同步关键类，构造方法传入的数字是多少，则同一个时刻，只运行多少个进程同时运行制定代码

	/**
	 * 在 semaphore.acquire() 和 semaphore.release()之间的代码，同一时刻只允许制定个数的线程进入，
	 * 因为semaphore的构造方法是5，则同一时刻只允许5个线程进入，其他线程只能等待。
	 */
	public void doSomething() {
		try {
			semaphore.acquire();
			System.out.println(Thread.currentThread().getName() + ":doSomething start-");
			Thread.sleep(5000);
			System.out.println(Thread.currentThread().getName() + ":doSomething end-");
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}