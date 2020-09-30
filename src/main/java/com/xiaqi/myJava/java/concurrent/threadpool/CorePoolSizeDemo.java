package com.xiaqi.myJava.java.concurrent.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 0、如果corePoolSize==0，会创建一个线程来执行任务<br>
 * 1、如果工作线程小于corePoolSize，则创建新线程来执行任务<br>
 * 2、如果运行的线程等于corePoolSize ,则将任务加入BlockingQueue；<br>
 * 3、如果队列已满并且正在运行的线程数量小于 maximumPoolSize，则创建新的线程来处理任务<br>
 * 4、如果当前运行的线程等于maxiumPoolSize，任务将被拒绝，并调用RejectedExecutionHandler.rejectedExecution()方法
 */
public class CorePoolSizeDemo {
    public static void main(String[] args) throws InterruptedException {
        // 使用只能5个有限队列，corePoolSize=5, maxPoolSize=10
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5));
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 创建4个线程
        for (int i = 0; i < 5; i++) {
            final int a = i;
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                    // largestPoolSize 该变量记录了线程池在整个生命周期中曾经出现的最大工作线程数
                    System.out.println(a + " -" + executor.getQueue().size() + " Largest:" + executor.getPoolSize());
                } catch (InterruptedException e) {
                }
            });
        }
        Thread.sleep(5000);
        
        executor.execute(() -> {
                System.out.println("boss -" + executor.getQueue().size() + " Largest:" + executor.getPoolSize());
        });
        executor.shutdown();
    }
}
