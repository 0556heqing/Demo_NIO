package com.heqing.netty.bean.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;


public class ProtobufClient {

	public void connect(String host, int port) throws Exception {  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
  
        try {  
            Bootstrap b = new Bootstrap();  
            b.group(workerGroup)
             .channel(NioSocketChannel.class)  
             .option(ChannelOption.TCP_NODELAY, true) 
             .handler(new ChannelInitializer<SocketChannel>() {  
                @Override  
                public void initChannel(SocketChannel ch) throws Exception {  
                	ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());	//处理半包
                	ch.pipeline().addLast(new ProtobufDecoder(ProtobufProto.testBuf.getDefaultInstance()));
                	ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                	ch.pipeline().addLast(new ProtobufEncoder());
                	// 注册handler
                    ch.pipeline().addLast(new ProtobufClientHandler());  
                }  
            });  
  
            // Start the client.  
            ChannelFuture f = b.connect(host, port).sync();  
  
            // Wait until the connection is closed.  
            f.channel().closeFuture().sync();  
        } finally {  
            workerGroup.shutdownGracefully();  
        }  
    }  
	
	public static void main(String[] args) throws Exception {  
		ProtobufClient client = new ProtobufClient();  
        client.connect("127.0.0.1", 8080);  
    }  
}
