package com.xiaqi.myJava.java.io.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Buffer(缓冲区)用于和Channel(通道)进行交互。数据是从通道读入Buffer，从Buffer写入到通道中.<br/>
 * Buffer本质上是一块可以写入数据，又可以从中读取数据的内存。这块内存被包装成NIO Buffer对象，并提供了一组方法，用来方便的访问该块内存
 * @author xiaqi
 *
 */
public class BufferDemo {
    public static void main(String[] args) {
        buffer();
    }

    /**
     * buffer基本用法，读取文件全部内容，打印出来
     */
    public static void buffer() {
        try {
            // RandomAccessFile file = new RandomAccessFile("E:\\111.txt", "rw");
            FileOutputStream fos = new FileOutputStream("E:\\111.txt");
            FileChannel channel = fos.getChannel();// 建立一个文件通道
            // 分配一个新的缓冲区，大小为1024byte
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int length = channel.read(buffer);// 读取数据到Buffer
            while (length != -1) {
                buffer.flip();// 位置重置为0
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.clear();
                length = channel.read(buffer);
            }
            channel.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * 从通道中读取数据到buffer数组的用法
     */
    public static void bufferArray() {
        try {
            RandomAccessFile file = new RandomAccessFile("E:\\111.txt", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buffer1 = ByteBuffer.allocate(10);
            ByteBuffer buffer2 = ByteBuffer.allocate(10);
            ByteBuffer[] bufferArray = { buffer1, buffer2 };
            long length = channel.read(bufferArray);
            System.out.println(length);
            buffer1.flip();// 位置重置为0
            while (buffer1.hasRemaining()) {
                System.out.print((char) buffer1.get());
            }
            buffer1.clear();
            channel.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
