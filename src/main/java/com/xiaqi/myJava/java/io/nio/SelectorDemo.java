package com.xiaqi.myJava.java.io.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 一个单独的线程可以通过Selector（选择器）监听多个channel，从而监听多个网络连接。
 * 
 * @author xiaqi
 *
 */
public class SelectorDemo {
	public static void main(String[] args) {
		selector();
	}

	/**
	 * Selector基本用法，将channel注册到selector上
	 */
	public static void selector() {
		try {
			Selector selector = Selector.open();
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);// 切换到非阻塞模式.与Selector一起使用时，Channel必须处于非阻塞模式下
			// register方法第二个参数,是一个“interest集合”,，意思是在通过Selector监听Channel时对什么事件感兴趣。
			// 可以监听四种不同类型的事件：1.Connect 2.Accept 3.Read 4.Write
			channel.register(selector, SelectionKey.OP_CONNECT);
		} catch (IOException e) {
		}
	}
}
