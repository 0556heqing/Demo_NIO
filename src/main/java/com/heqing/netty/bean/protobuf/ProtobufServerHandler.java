package com.heqing.netty.bean.protobuf;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ProtobufServerHandler extends ChannelHandlerAdapter {
	
	@Override  
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { 
		ProtobufProto.testBuf message = (ProtobufProto.testBuf)msg;
		System.out.println("send---->"+message.toString());
		ProtobufProto.testBuf.Builder builder = ProtobufProto.testBuf.newBuilder();
		builder.setId(2l);
		builder.setReceive(10002l);
		builder.setSend(10001l);
		builder.setContent("this is server");
		builder.setType(0);
		builder.setState(0);
		builder.setCreateTime(System.currentTimeMillis());
        ctx.writeAndFlush(builder.build());      
	}
	
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        // 当出现异常就关闭连接  
        cause.printStackTrace();  
        ctx.close();  
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
        ctx.flush();  
    }  
}
