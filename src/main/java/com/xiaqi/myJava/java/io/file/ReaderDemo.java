package com.xiaqi.myJava.java.io.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

public class ReaderDemo {
	public static void main(String[] args) throws Exception {
		buffer();
	}

	public static void buffer() throws Exception {
		File file = new File("e:/buffered.txt");
		Writer writer = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(writer);
		bw.write("1234\n");
		bw.write("2345\n");
		bw.write("3456\n");
		bw.write("\n");
		bw.write("4567\n");
		bw.close();
		writer.close();

		if (file.exists() && file.getName().endsWith(".txt")) {
			Reader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			while ((str = br.readLine()) != null) {
				System.out.println(str);
			}
			reader.close();
			br.close();
		}
	}
	
	public static void read()  throws Exception {
		File file = new File("e:/writer.txt");
		Writer out = new FileWriter(file);
		// ����һ��String���Ͷ���
		String str = "Hello World!!!";
		out.write(str);
		out.close();

		// ���ļ�����
		Reader in = new FileReader(file);
		// ����һ���ռ����ڽ����ļ�������������
		char c0[] = new char[1024];
		int i = 0;
		// ��c0�����ô��ݵ�read()����֮�У�ͬʱ�˷������ض������ݵĸ���
		i = in.read(c0);
		in.close();

		if (-1 == i) {
			System.out.println("�ļ���������");
		} else {
			System.out.println(new String(c0, 0, i));
		}
	}
}
