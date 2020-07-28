package com.xiaqi.myJava.java.jvm.gc;

public class GCTest {
    public Object instance = null;
    private static final int _1MB = 1024 * 1024;
    private byte[] bigSize = new byte[2 * _1MB];

    public static void testGC() {
        GCTest objA = new GCTest();
        GCTest objB = new GCTest();
        objA.instance = objB;
        objB.instance = objA;
        objA = null;
        objB = null;
    }

    public static void main(String[] args) {

    }

}
