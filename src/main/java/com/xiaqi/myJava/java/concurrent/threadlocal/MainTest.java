package com.xiaqi.myJava.java.concurrent.threadlocal;

public class MainTest {
	public static void main(String[] args) throws Exception {
		ThreadLocalThread a = new ThreadLocalThread("ThreadA");
		ThreadLocalThread b = new ThreadLocalThread("ThreadB");
		a.start();
		b.start();
	}
}

class ThreadLocalThread extends Thread {

    public ThreadLocalThread(String name) {
        super(name);
    }

    public void run() {
        try {
            for (int i = 0; i < 3; i++) {
                Tools.t1.set("hello world" + i);
                Tools.t2.set(i);
                System.out.println(this.getName() + " get value--->" + Tools.t1.get());
                System.out.println(this.getName() + " get value--->" + Tools.t2.get());
                //Thread.sleep(200);
            }
        } catch (Exception e) {
        }
    }
}

class Tools {
    public static ThreadLocal<String> t1 = new ThreadLocal<String>();
    public static ThreadLocal<Integer> t2 = new ThreadLocal<Integer>();
}