package com.xiaqi.myJava.java.io.bio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * BIO Channel版本
 *
 */
public class BIOChannelServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(6666));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (true) {
            // 调用accept()方法将无限期阻塞程序，直到有新的client连接
            SocketChannel client = server.accept();
            new Thread(() -> {
                try {
                    while (client.read(buffer) != -1) {
                        System.out.println(new String(buffer.array()));
                        buffer.clear();
                    }
                } catch (IOException e) {
                }
            }).start();
        }
    }

}
