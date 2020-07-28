package com.xiaqi.myJava.java.jvm.oom;

/**
 * 栈内存溢出异常
 *
 */
public class StackOOM {
    public static void main(String[] args) {
        while(true) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {}
                }
            });
            t.start();
        }
    }
}
