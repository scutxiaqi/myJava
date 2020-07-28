package com.xiaqi.myJava.java.concurrent.atomic;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicLogTest implements Runnable {
	private static AtomicInteger count = new AtomicInteger(0);
	private CyclicBarrier barrier;

	public AtomicLogTest(CyclicBarrier barrier) {
		this.barrier = barrier;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10000; i++) {
			count.incrementAndGet();
		}
		try {
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CyclicBarrier barrier = new CyclicBarrier(2, new Runnable() {
			@Override
			public void run() {
				System.out.println(count);
			}
		});
		for (int i = 0; i < 2; i++) {
			Thread thread = new Thread(new AtomicLogTest(barrier));
			thread.start();
		}
	}
}
