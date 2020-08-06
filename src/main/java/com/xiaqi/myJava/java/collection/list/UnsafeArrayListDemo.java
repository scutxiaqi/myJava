package com.xiaqi.myJava.java.collection.list;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList在多线程下执行，会出现如下问题：<br>
 * 1.数据丢失 <br>
 * 2.出现null值 <br>
 * 3.抛出异常 <br>
 */
public class UnsafeArrayListDemo {
    public static List<String> arrayList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Thread[] threadArray = new Thread[1000];
        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i] = new MyThread();
            threadArray[i].start();
        }

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i].join();
        }

        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i));
        }

    }
}

class MyThread extends Thread {
    public void run() {
        try {
            Thread.sleep(1000);
            UnsafeArrayListDemo.arrayList.add(Thread.currentThread().getName());
        } catch (InterruptedException e) {
        }
    }
}
