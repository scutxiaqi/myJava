package com.xiaqi.myJava.java.io.bio.channel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
/**
 * BIO Channel版本
 *
 */
public class BIOChannelClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 无限循环，模拟多个客户端连接服务端
        while (true) {
            buffer.put((new Date() + ": hello world").getBytes());
            // 在读之前都要切换成读模式
            buffer.flip();
            socketChannel.write(buffer);
            // 读完切换成写模式，能让管道继续读取文件的数据
            buffer.clear();

            Thread.sleep(10000);
        }
    }
}
