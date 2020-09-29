package com.xiaqi.myJava.java.concurrent.collection.queue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue8<E> {
    /**
     * 内部数组
     */
    final Object[] items;
    /**
     * 下一个待删除位置的索引: take, poll, peek, remove方法使用
     */
    int takeIndex;
    /**
     * 下一个待插入位置的索引: put, offer, add方法使用
     */
    int putIndex;
    /**
     * 队列中的元素个数
     */
    int count;
    /**
     * 全局锁，元素在入列和出列时都要用到，生产者-消息者共用。超高并发的环境，可能出现性能瓶颈
     */
    final ReentrantLock lock;
    /**
     * 非空条件队列：当队列空时，线程在该队列等待获取
     */
    private final Condition notEmpty;
    /**
     * 非满条件队列：当队列满时，线程在该队列等待插入
     */
    private final Condition notFull;

    transient Itrs itrs = null;

    /**
     * 指定队列初始容量的构造器.
     */
    public ArrayBlockingQueue8(int capacity) {
        this(capacity, false);
    }

    /**
     * 指定队列初始容量和公平/非公平策略的构造器.
     */
    public ArrayBlockingQueue8(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();

        this.items = new Object[capacity];
        lock = new ReentrantLock(fair); // 利用独占锁的策略
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    /**
     * 在队尾插入指定元素，如果队列已满，则阻塞线程.
     */
    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly(); // 加锁
        try {
            //队列已满的时候，通过while循环判断,这其实是多线程设计模式中的Guarded Suspension模式
            while (count == items.length) // 队列已满。这里必须用while，防止虚假唤醒
                notFull.await(); // 在notFull队列上等待
            enqueue(e); // 队列未满, 直接入队
        } finally {
            lock.unlock();
        }
    }
    
    private void enqueue(E x) {
        final Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length) // 队列已满,则重置索引为0
            putIndex = 0;
        count++; // 元素个数+1
        notEmpty.signal(); // 唤醒一个notEmpty上的等待线程(可以来队列取元素了)
    }

    /**
     * 从队首删除一个元素, 如果队列为空, 则阻塞线程
     */
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) // 队列为空, 则线程在notEmpty条件队列等待
                notEmpty.await();
            return dequeue(); // 队列非空，则出队一个元素
        } finally {
            lock.unlock();
        }
    }

    private E dequeue() {
        final Object[] items = this.items;
        E x = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length) // 如果队列已空
            takeIndex = 0;
        count--;
        if (itrs != null)
            itrs.elementDequeued();
        notFull.signal(); // 唤醒一个notFull上的等待线程(可以插入元素到队列了)
        return x;
    }

    /**
     * 检查非空。如果为null，抛出异常
     * 
     * @param v
     */
    private static void checkNotNull(Object v) {
        if (v == null)
            throw new NullPointerException();
    }

    class Itrs {
        void elementDequeued() {
        }
    }
}
