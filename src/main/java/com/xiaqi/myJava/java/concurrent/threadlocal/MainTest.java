package com.xiaqi.myJava.java.concurrent.threadlocal;

public class MainTest {
	public static void main(String[] args) throws Exception {
		ThreadLocalThread a = new ThreadLocalThread("ThreadA");
		ThreadLocalThread b = new ThreadLocalThread("ThreadB");
		a.start();
		b.start();
	}
}
