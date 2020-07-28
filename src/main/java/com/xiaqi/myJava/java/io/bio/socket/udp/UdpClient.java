package com.xiaqi.myJava.java.io.bio.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClient {

	public static void main(String[] args) {
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
		}
		String hello = "hello world";
		try {
			packet = new DatagramPacket(hello.getBytes(), 10, InetAddress.getByName("localhost"), 9999);
		} catch (UnknownHostException e) {
		}

		try {
			socket.send(packet);
		} catch (IOException e) {
		}
		socket.close();
	}

}
