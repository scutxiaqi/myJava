package com.xiaqi.myJava.java.concurrent.threadlocal;

public class ThreadLocalThread extends Thread {

	public ThreadLocalThread(String name) {
		super(name);
	}

	public void run() {
		try {
			for (int i = 0; i < 3; i++) {
				Tools.t1.set(i + "");
				System.out.println(this.getName() + " get value--->" + Tools.t1.get());
				Thread.sleep(200);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
