package com.xiaqi.myJava.java.io.bio.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO Socket版本
 *
 */
public class Server {
    public static void main(String[] args) throws IOException {
        // 服务端处理客户端连接请求
        ServerSocket server = new ServerSocket(3333);
        while (true) {
            try {
                Socket client = server.accept();// 调用accept()方法将无限期阻塞程序，直到有新的client连接
                // 为每一个新的连接都创建一个线程
                new Thread(() -> {
                    try {
                        int len;
                        byte[] data = new byte[1024];
                        InputStream inputStream = client.getInputStream();
                        // 按字节流方式读取数据
                        while ((len = inputStream.read(data)) != -1) {
                            System.out.println(new String(data, 0, len));
                        }
                    } catch (IOException e) {
                    }
                }).start();
            } catch (IOException e) {
            }
        }
    }
}
