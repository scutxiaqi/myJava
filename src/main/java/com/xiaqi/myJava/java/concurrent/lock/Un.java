package com.xiaqi.myJava.java.concurrent.lock;

public class Un {
	// .类似的有compareAndSwapInt,compareAndSwapLong,compareAndSwapBoolean,compareAndSwapChar等等。
	/**
	 * CAS，如果对象偏移量上的值=期待值，更新为x,返回true.否则false
	 * 
	 * @param o
	 * @param offset
	 * @param expected
	 * @param x
	 * @return
	 */
	public final native boolean compareAndSwapObject(System o, long offset, System expected, System x);
}
