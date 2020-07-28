package com.xiaqi.myJava.java.concurrent.tool;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch应用场景:一组线程先执行完毕，交给另一个线程去做汇总<br/>
 * 举个例子，三个工人干活，活都干完了的时候，老板来检查工人所干的活。
 * @author xiaqi
 *
 */
public class MyCountDownLatch {

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(3);
		
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(new Worker(latch, "张三"));
		executor.execute(new Worker(latch, "李四"));
		executor.execute(new Worker(latch, "王二"));
		executor.execute(new Boss(latch));
		executor.shutdown();
	}

}

class Worker implements Runnable {

	private CountDownLatch latch;
	private String name;

	public Worker(CountDownLatch latch, String name) {
		this.latch = latch;
		this.name = name;
	}

	public void run() {
		doWork();
		try {
			TimeUnit.SECONDS.sleep(new Random().nextInt(10));
		} catch (InterruptedException ie) {
		}
		System.out.println(this.name + "活干完了！");
		latch.countDown();
	}

	private void doWork() {
		System.out.println(this.name + "正在干活!");
	}
}

class Boss implements Runnable {

	private CountDownLatch latch;

	public Boss(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		System.out.println("工人活都干完了，老板开始检查了！");
	}
}