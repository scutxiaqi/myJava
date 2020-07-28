package com.xiaqi.myJava.java.jvm.oom;

/**
 * StackOverflowError
 *
 */
public class StackSOF {
    private int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        StackSOF item = new StackSOF();
        item.stackLeak();
    }
}
