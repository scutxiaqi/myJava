package com.xiaqi.myJava.other.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 启动一个Netty服务端.指定三类属性:线程模型、IO模型、IO读写处理逻辑(解码器、处理器等)
 *
 */
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup(); // 监听IO连接的线程组。老板负责从外面接活，员工负责具体干活
        NioEventLoopGroup worker = new NioEventLoopGroup(); // 处理IO读写的线程组
        // 引导类，这个类将引导我们进行服务端启动初始化工作
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //.channel指定我们服务端的IO模型为NIO。NioServerSocketChannel和NioSocketChannel的概念对应java NIO编程模型中的ServerSocketChannel和SocketChannel
        //NioSocketChannel为Netty对NIO连接的抽象。
        // ChannelInitializer定义IO读写，业务处理逻辑
        serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            protected void initChannel(NioSocketChannel ch) {
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                        System.out.println(msg);
                    }
                });
            }
        }).bind(6666);
    }
}
