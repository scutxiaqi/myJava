package com.xiaqi.myJava.java.concurrent.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * maxPoolSize表示线程池中允许的最大线程数。
 * 优先使用队列，如果队列满了，才使用maxPoolSize。
 * 无界队列（比如：LinkedBlockingQueue），可以创建无穷个task，直到内存溢出，maxPoolSize永远用不上
 */
public class MaxPoolSize {
    public static void main(String[] args) {
        // 使用只能5个有限队列，corePoolSize=2, maxPoolSize=10
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 创建15个线程
        for (int i = 0; i < 15; i++) {
            final int a = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(500);
                    // largestPoolSize 该变量记录了线程池在整个生命周期中曾经出现的最大线程个数
                    System.out.println(a + " -" + executor.getQueue().size() + " Largest:" + executor.getLargestPoolSize());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
    }
}
