package com.xiaqi.myJava.java.io.file;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipedInputStreamDemo {
	public static void main(String[] args) {
		try {
			Sender sender = new Sender();
			Receiver receiver = new Receiver();
			Thread senderThread = new Thread(sender);
			Thread receiverThread = new Thread(receiver);
			PipedOutputStream out = sender.getOutputStream(); // д��
			PipedInputStream in = receiver.getInputStream(); // ����
			out.connect(in);// ��������͵�����
			senderThread.start();
			receiverThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Receiver implements Runnable {
	private PipedInputStream in = new PipedInputStream();

	public PipedInputStream getInputStream() {
		return in;
	}

	public void run() {
		String s = null;
		byte b0[] = new byte[2];
		try {
			int length = in.read(b0);
			while (-1 != length) {
				s = new String(b0, 0, length);
				System.out.print(s);
				length = in.read(b0);
			}
			//in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Sender implements Runnable {
	private PipedOutputStream out = new PipedOutputStream();

	public PipedOutputStream getOutputStream() {
		return out;
	}

	public void run() {
		String str = "Receiver, ���!";
		try {
			out.write(str.getBytes()); // ��ܵ�����д�����ݣ����ͣ�
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
