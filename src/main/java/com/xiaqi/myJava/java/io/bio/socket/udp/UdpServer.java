package com.xiaqi.myJava.java.io.bio.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServer {
	public static void main(String[] args) {
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket(9999);
		} catch (SocketException e) {
		}
		byte[] buf = new byte[1024];
		packet = new DatagramPacket(buf, 10);
		try {
			socket.receive(packet);
		} catch (IOException e) {
		}
		System.out.println(new String(buf));
		socket.close();
	}
}
