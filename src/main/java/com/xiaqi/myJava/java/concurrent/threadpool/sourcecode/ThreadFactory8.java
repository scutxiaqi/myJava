package com.xiaqi.myJava.java.concurrent.threadpool.sourcecode;

/**
 * 线程工厂，用于创建单个线程，减少手工创建线程的繁琐工作，同时能够复用工厂的特性。
 */
public interface ThreadFactory8 {
    Thread newThread(Runnable r);
}
