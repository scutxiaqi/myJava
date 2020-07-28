package com.xiaqi.myJava.java.concurrent.tool;

import java.util.concurrent.locks.LockSupport;

/**
 * 线程阻塞唤醒工具类
 *
 */
public class LockSupportDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new ParkThread());
        t.start();
        Thread.sleep(1000);
        LockSupport.unpark(t);// 唤醒指定线程，类似于notify()方法

    }

    static class ParkThread implements Runnable {
        @Override
        public void run() {
            System.out.println("开始线程");
            LockSupport.park();// 类似于wait()方法
            System.out.println("结束线程");
        }
    }
}
