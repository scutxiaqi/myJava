package com.xiaqi.myJava.java.io.file;

import java.io.RandomAccessFile;

public class StreamDemo {

	public static void main(String[] args) throws Exception {
		RandomAccessFile in = new RandomAccessFile("D:\\file.txt","r");
		in.skipBytes(9);
		int c = in.readByte();
		System.out.print((char) c);
	}

}
