package com.heqing.netty.bean.protobuf;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ProtobufClientHandler extends ChannelHandlerAdapter{
	
	// 连接成功后，向server发送消息    
    @Override    
    public void channelActive(ChannelHandlerContext ctx) throws Exception {    
    	ProtobufProto.testBuf.Builder builder = ProtobufProto.testBuf.newBuilder();
		builder.setId(1l);
		builder.setReceive(10001l);
		builder.setSend(10002l);
		builder.setContent("this is send");
		builder.setType(0);
		builder.setState(0);
		builder.setCreateTime(System.currentTimeMillis());
        ctx.writeAndFlush(builder.build());      
    }    
	
	@Override  
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { 
		ProtobufProto.testBuf message = (ProtobufProto.testBuf)msg;
		System.out.println("receive---->"+message.toString());
	}
	
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        // 当出现异常就关闭连接  
        cause.printStackTrace();  
        ctx.close();  
    }
}
