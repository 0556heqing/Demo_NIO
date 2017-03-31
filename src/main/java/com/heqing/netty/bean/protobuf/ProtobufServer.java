package com.heqing.netty.bean.protobuf;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProtobufServer {
  
    public void bind(int port) throws Exception {  
        //EventLoopGroup是用来处理IO操作的多线程事件循环器  
        //bossGroup 用来接收进来的连接  
        EventLoopGroup bossGroup = new NioEventLoopGroup();   
        //workerGroup 用来处理已经被接收的连接  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            //启动 NIO 服务的辅助启动类  
            ServerBootstrap b = new ServerBootstrap();   
            b.group(bossGroup, workerGroup)  
                //配置 Channel  
                .channel(NioServerSocketChannel.class)  
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {   
                        @Override  
                        public void initChannel(SocketChannel ch) throws Exception {  
                        	ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());	//处理半包
                        	ch.pipeline().addLast(new ProtobufDecoder(ProtobufProto.testBuf.getDefaultInstance()));
                        	ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        	ch.pipeline().addLast(new ProtobufEncoder());
                        	// 注册handler    
                            ch.pipeline().addLast(new ProtobufServerHandler());  
                        }  
                    })  
                .childOption(ChannelOption.SO_KEEPALIVE, true);   
  
            // 绑定端口，开始接收进来的连接  
            ChannelFuture f = b.bind(port).sync();  
            // 等待服务器 socket 关闭 。  
            f.channel().closeFuture().sync();  
        } finally {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }  
      
    public static void main(String[] args) throws Exception {  
    	int port = 8080;
        new ProtobufServer().bind(port); 
    }  
}
