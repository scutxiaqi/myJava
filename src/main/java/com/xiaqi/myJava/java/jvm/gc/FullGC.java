package com.xiaqi.myJava.java.jvm.gc;

public class FullGC {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(30000); // main线程休眠30s，以便jstat命令通过PID观察

        while (true) {
            loadData();
        }
    }

    private static void loadData() throws InterruptedException {
        byte[] data = null;
        for (int i = 0; i < 4; i++) {
            data = new byte[10 * 1024 * 1024]; // 10MB
        }
        data = null;

        byte[] data1 = new byte[10 * 1024 * 1024];
        byte[] data2 = new byte[10 * 1024 * 1024];

        byte[] data3 = new byte[10 * 1024 * 1024];
        data3 = new byte[10 * 1024 * 1024];

        Thread.sleep(1000); // 模拟上述操作全部发生在1s内
    }
}
