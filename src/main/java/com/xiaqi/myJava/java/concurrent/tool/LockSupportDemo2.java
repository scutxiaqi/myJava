package com.xiaqi.myJava.java.concurrent.tool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo2 {
    public static void main(String[] args) throws InterruptedException {
        FIFOMutex mutex = new FIFOMutex();
        MyThread1 a1 = new MyThread1("a1", mutex);
        MyThread1 a2 = new MyThread1("a2", mutex);
        MyThread1 a3 = new MyThread1("a3", mutex);

        a1.start();
        a2.start();
        a3.start();

        a1.join(); // 等待这个线程死亡
        a2.join();
        a3.join();

        System.out.print("Finished");
    }
}

class MyThread1 extends Thread {
    private String name;
    private FIFOMutex mutex;
    public static int count;

    public MyThread1(String name, FIFOMutex mutex) {
        this.name = name;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            mutex.lock();
            count++;
            System.out.println("name:" + name + "  count:" + count);
            mutex.unlock();
        }
    }
}

class FIFOMutex {
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<Thread>();

    public void lock() {
        Thread current = Thread.currentThread();
        waiters.add(current);
        // 如果当前线程不在队首，或锁已被占用，则当前线程阻塞
        // NOTE：这个判断的意图其实就是：锁必须由队首元素拿到
        while (waiters.peek() != current || !locked.compareAndSet(false, true)) {
            LockSupport.park(this);
        }
        waiters.remove(); // 删除队首元素
    }

    public void unlock() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }
}
