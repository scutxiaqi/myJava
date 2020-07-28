package com.xiaqi.myJava.java.jvm.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * 堆内存溢出异常
 *
 */
public class HeapOOM {
    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
        }
    }
}
