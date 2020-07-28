package com.xiaqi.myJava.java.concurrent.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *0、如果corePoolSize==0，会创建一个线程来执行任务
 *1、如果当前运行的线程少于corePoolSize，则创建新线程来执行任务（需要获得全局锁）；
 *2、如果运行的线程等于corePoolSize ,则将任务加入BlockingQueue；
 *3、如果队列已满并且正在运行的线程数量小于 maximumPoolSize，则创建新的线程来处理任务（需要获得全局锁）
 *4、如果当前运行的线程等于maxiumPoolSize，任务将被拒绝，并调用RejectedExecutionHandler.rejectedExecution()方法
 */
public class CorePoolSize {
    public static void main(String[] args) {
        // 使用只能5个有限队列，corePoolSize=5, maxPoolSize=10
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 创建4个线程
        for (int i = 0; i < 4; i++) {
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
