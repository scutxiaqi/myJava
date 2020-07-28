package com.xiaqi.myJava.java.io.bio.threadpool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO 线程池版本
 *
 */
public class BIOThreadPoolServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(6666));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        while (true) {
            // 调用accept()方法将无限期阻塞程序，直到有新的client连接
            SocketChannel client = server.accept();
            executor.submit(() -> {
                try {
                    while (client.read(buffer) != -1) {
                        System.out.println(new String(buffer.array()));
                        buffer.clear();
                    }
                } catch (IOException e) {
                }
            });
        }
    }

}
