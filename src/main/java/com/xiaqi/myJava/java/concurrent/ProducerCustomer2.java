package com.xiaqi.myJava.java.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 利用await()/signal()实现生产者和消费者模型
 */
public class ProducerCustomer2 {
	public static String value = "";// 临界区，value为""表示无产品；value不为""表示有产品

	public static void main(String[] args) {
		final ProducerCustomerDemo pc = new ProducerCustomerDemo();
		Runnable producerRunnable = new Runnable() {
			public void run() {
				for (int i = 0; i < Integer.MAX_VALUE; i++)
					pc.set();
			}
		};
		Runnable customerRunnable = new Runnable() {
			public void run() {
				for (int i = 0; i < Integer.MAX_VALUE; i++)
					pc.get();
			}
		};
		Thread ProducerThread = new Thread(producerRunnable);
		Thread ConsumerThread = new Thread(customerRunnable);
		ProducerThread.start();
		ConsumerThread.start();
	}
}

class ProducerCustomerDemo extends ReentrantLock {
	private Condition condition = newCondition();

	public void set() {
		try {
			lock();
			while (!"".equals(ProducerCustomer2.value))
				condition.await();
			ProducerCustomer2.value = "123";
			condition.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			unlock();
		}
	}

	public void get() {
		try {
			lock();
			while ("".equals(ProducerCustomer2.value))
				condition.await();
			ProducerCustomer2.value = "";
			condition.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			unlock();
		}
	}
}
