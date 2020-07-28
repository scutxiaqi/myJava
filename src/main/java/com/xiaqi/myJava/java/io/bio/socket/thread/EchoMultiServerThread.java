package com.xiaqi.myJava.java.io.bio.socket.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoMultiServerThread extends Thread {
	private Socket socket = null;

	public EchoMultiServerThread(Socket socket) {
		super("EchoMultiServerThread");
		// 声明一个 socket 对象
		this.socket = socket;
	}

	public void run() {
		try {
			PrintWriter out = null;
			BufferedReader in = null;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 提示信息
			out.println("Hello!...");
			out.println("Enter BYE to exit");
			out.flush();
			// 没有异常则不断循环
			while (true) {
				// 只有当用户输入时才返回数据
				String str = in.readLine();
				// 当用户连接断掉时会返回空值null
				if (str == null) {
					// 退出循环
					break;
				} else {
					// 对用户输入字符串加前缀Echo并将此信息打印到客户端
					out.println("Echo：" + str);
					out.flush();
					// 退出命令，equalsIgnoreCase()是不区分大小写的
					if ("BYE".equalsIgnoreCase(str.trim())) {
						break;
					}
				}
			}
			// 该close的资源都close掉
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {

		}
	}
}
