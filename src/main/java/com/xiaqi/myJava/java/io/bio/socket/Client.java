package com.xiaqi.myJava.java.io.bio.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws IOException {
        // 无限循环，模拟多个客户端连接服务端
        try {
            Socket client = new Socket("127.0.0.1", 6666);
            while (true) {
                try {
                    client.getOutputStream().write((new Date() + ": hello world").getBytes());
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
        }

    }
}
