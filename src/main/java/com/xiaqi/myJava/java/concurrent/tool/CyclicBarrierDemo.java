package com.xiaqi.myJava.java.concurrent.tool;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier应用场景:一组线程执行，在公共屏障点 (common barrier point)等待所有线程都到达，再继续执行。<br/>
 * 举个例子，赛跑时，等待所有人都准备好时，才开枪起跑.
 * 
 * @author xiaqi
 */
public class CyclicBarrierDemo {

	public static void main(String[] args) throws IOException, InterruptedException {
		CyclicBarrier barrier = new CyclicBarrier(3);

		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runner(barrier, "1号选手"));
		executor.execute(new Runner(barrier, "2号选手"));
		executor.execute(new Runner(barrier, "3号选手"));

		executor.shutdown();
	}
}

class Runner implements Runnable {
	private CyclicBarrier barrier;

	private String name;

	public Runner(CyclicBarrier barrier, String name) {
		super();
		this.barrier = barrier;
		this.name = name;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000 * (new Random()).nextInt(8));
			System.out.println(name + " 准备好了...");
			barrier.await();// barrier的await方法，在所有参与者都已经在此 barrier 上调用 await
							// 方法之前，将一直等待。
		} catch (InterruptedException e) {
		} catch (BrokenBarrierException e) {
		}
		System.out.println(name + " 起跑！");
	}
}
