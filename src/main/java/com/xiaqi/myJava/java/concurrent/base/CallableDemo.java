package com.xiaqi.myJava.java.concurrent.base;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        FutureTask<String> task =  new FutureTask<String>(new MyThread2());
        new Thread(task).start();
        System.out.println("线程返回值：" + task.get());
    }
}

class MyThread2 implements Callable<String> {
    @Override
    public String call() throws Exception {
        for (int x = 0; x < 10; x++) {
            System.out.println("******线程执行，x = " + x);
        }
        return "线程执行完毕！";
    }
}
