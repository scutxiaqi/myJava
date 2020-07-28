package com.xiaqi.myJava.java.concurrent;

/**
 * 利用wait()/notify()实现生产者/消费者模型
 * 
 * @author xiaqi
 *
 */
public class ProducerCustomer {
	public static String value = "";// 临界区，value为""表示无产品；value不为""表示有产品

	public static void main(String[] args) {
	    Object lock = new Object();
		Thread producerThread = new Thread(new Producer(lock));
		Thread CustomerThread = new Thread(new Customer(lock));
		producerThread.start();
		CustomerThread.start();
	}
}

class Producer implements Runnable {
	private Object lock;

	public Producer(Object lock) {
		this.lock = lock;
	}

	public void run() {
		while (true) {
			try {
				synchronized (lock) {
					if (!ProducerCustomer.value.equals(""))
						lock.wait();
					String value = System.currentTimeMillis() + "_" + System.nanoTime();
					System.out.println("Set的值是：" + value);
					ProducerCustomer.value = value;
					lock.notify();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}

class Customer implements Runnable {
	private Object lock;

	public Customer(Object lock) {
		this.lock = lock;
	}

	public void run() {
		while (true) {
			try {
				synchronized (lock) {
					if (ProducerCustomer.value.equals(""))
						lock.wait();
					System.out.println("Get的值是：" + ProducerCustomer.value);
					ProducerCustomer.value = "";
					lock.notify();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
