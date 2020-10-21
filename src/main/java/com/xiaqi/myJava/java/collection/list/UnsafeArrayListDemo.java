package com.xiaqi.myJava.java.collection.list;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList在多线程下添加元素，会出现如下问题：<br>
 * 1.数据丢失 <br>
 * 2.出现null值 <br>
 * 3.抛出异常 <br>
 * 原因：add方法分3步骤：1.校验数组容量 2.添加元素到list中 3.list大小增加1。<br>
 * 假设list大小为9，数组长度是10。线程A和线程B依次执行完第1步，线程A执行完第2、3步，此时数组已经满了，线程B再执行第2步，会导致数组下标越界异常。<br>
 * 假设list大小为5，数组长度是10。线程A和线程B依次执行完第2步，线程A的数据丢失（被线程B覆盖）。<br>
 * 线程A和线程B执行完毕后，因为线程B执行的数组下标有误，导致list最末尾的元素是null值
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
