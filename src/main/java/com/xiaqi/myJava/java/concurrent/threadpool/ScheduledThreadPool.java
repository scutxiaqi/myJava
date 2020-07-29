package com.xiaqi.myJava.java.concurrent.threadpool;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPool {

    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        scheduleAtFixedRate(service, 1000);
        // scheduleAtFixedRate(service,6000);
    }

    private static void scheduleAtFixedRate(ScheduledExecutorService service, final int sleepTime) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long start = new Date().getTime();
                System.out.println("scheduleAtFixedRate 开始执行时间:" + DateFormat.getTimeInstance().format(new Date()));
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long end = new Date().getTime();
                System.out.println("scheduleAtFixedRate 执行花费时间=" + (end - start) / 1000 + "m");
                System.out.println("scheduleAtFixedRate 执行完成时间：" + DateFormat.getTimeInstance().format(new Date()));
                System.out.println("======================================");
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
