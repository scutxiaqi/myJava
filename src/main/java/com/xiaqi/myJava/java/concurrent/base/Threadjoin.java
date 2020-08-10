package com.xiaqi.myJava.java.concurrent.base;

/**
 * Thread.join()用法<br>
 * 让10个线程按照顺序打印 0123456789
 */
public class Threadjoin {
    public static void main(String[] args) {
        Thread previousThread = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            Threadj t = new Threadj(previousThread, i);
            t.start();
            previousThread = t;
        }
    }
}

class Threadj extends Thread {
    int i;
    Thread previousThread; // 上一个线程

    public Threadj(Thread previousThread, int i) {
        this.previousThread = previousThread;
        this.i = i;
    }

    @Override
    public void run() {
        try {
            // 当前线程需要等待previousThread线程终止,才执行下一步
            previousThread.join();
        } catch (InterruptedException e) {
        }
        System.out.println("num:" + i);
    }
}
