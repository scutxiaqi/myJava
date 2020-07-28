package com.xiaqi.myJava.java.io.bio.socket.thread;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 应用多线程实现服务器与多客户端之间的通信 
 * 1.服务器端创建ServerSocket，循环调用accept()等待客户端连接
 * 2.客户端创建一个socket并请求和服务器端连接
 * 3.服务器端接受客户端请求，创建socket与该客户建立专线连接
 * 4.建立连接的两个socket在一个单独的线程上对话 
 * 5. 服务器端继续等待新的连接
 */
public class EchoServerThread {
	public static void main(String[] args) throws IOException {
		// 声明一个 serverSocket
		ServerSocket serverSocket = null;
		// 声明一个监听标识
		boolean listening = true;
		try {
			// 实例化监听端口
			serverSocket = new ServerSocket(1111);
		} catch (IOException e) {
			System.err.println("Could not listen on port:1111");
			System.exit(1);
		}
		// 如果处于监听态则开启一个线程
		while (listening) {
			// 实例化一个服务端的 socket 与请求 socket 建立连接
			new EchoMultiServerThread(serverSocket.accept()).start();
		}
		// 将 serverSocket 的关闭操作放在循环外，
		// 只有当监听为 false 是，服务才关闭
		serverSocket.close();
	}
}
