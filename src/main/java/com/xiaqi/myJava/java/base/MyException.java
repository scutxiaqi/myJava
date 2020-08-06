package com.xiaqi.myJava.java.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * java常见异常
 *
 */
public class MyException {
	public static void main(String[] args) throws Exception {
	    ClassNotFoundException();
	}

	/**
	 * 类型转换异常
	 */
	public static void ClassCastException() {
	    Object obj = new String("ddd");
	    Integer ii = (Integer) obj;
	}
	
	/**
     * 类找不到异常
     */
    public static void ClassNotFoundException() throws ClassNotFoundException {
        Class.forName("com.xiaqi.myJava.MyJavaApplication");// 这样写找到了
    }
    
    /**
     * 文件找不到
     */
    public static void FileNotFoundException() throws FileNotFoundException {
        InputStream is = new FileInputStream("C:\\111.txt");
    }
}
