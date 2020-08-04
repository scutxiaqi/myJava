package com.xiaqi.myJava.java.jvm.gc;

public class YoungGC {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(30000); // main线程休眠30s，以便jstat命令通过PID观察
        while (true) {
            loadData();
        }
    }

    private static void loadData() throws InterruptedException {
        byte[] data = null;
        for (int i = 0; i < 50; i++) {
            data = new byte[100 * 1024]; // 100KB
        }
        data = null;
        Thread.sleep(1000); // 模拟上述操作全部发生在1s内
    }
}
