package com.xiaqi.myJava.java.io.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 当新的连接完成（或失败）时调用该处理程序
 *
 */
public class ConnectHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {
    @Override
    public void completed(AsynchronousSocketChannel client, Attachment attachment) {
        AsynchronousServerSocketChannel server = attachment.getServer();
        // 收到新的连接后，server 应该重新调用 accept方法等待新的连接进来
        server.accept(attachment, this);
        try {
            System.out.println("收到新的连接：" + client.getRemoteAddress());
            Attachment newAtt = new Attachment();
            newAtt.setServer(server);
            newAtt.setClient(client);
            newAtt.setReadMode(true);
            newAtt.setBuffer(ByteBuffer.allocate(2048));
            client.read(newAtt.getBuffer(), newAtt, new ReadHandler());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable t, Attachment att) {
        System.out.println("accept failed");
    }
}
