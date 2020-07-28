package com.xiaqi.myJava.java.collection.map;

import java.util.HashMap;

/**
 * HashMap在多线程中遇到的问题
 * 
 * @author 123456
 *
 */
public class ThreadQuestion {
    //初始化为一个长度为2的数组，loadFactor=0.75，threshold=2*0.75=1，也就是说当put第二个key的时候，map就需要进行resize
    private static HashMap<Integer, String> map = new HashMap<Integer, String>(2, 0.75f);
    
    public static void main(String[] args) {
        infiniteLoop();
    }

    /**
     * 死循环
     */
    public static void infiniteLoop() {
        map.put(5, "C");

        new Thread("Thread1") {
            public void run() {
                map.put(7, "B");
                System.out.println(map);
            };
        }.start();
        new Thread("Thread2") {
            public void run() {
                map.put(3, "A");
                System.out.println(map);
            };
        }.start();
    }
}
