package com.heqing.netty.bean.marshalling;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.heqing.netty.bean.Message;

@Sharable
public class MarshallingServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message req = (Message) msg;
		System.out.println("this send msg --> ["+ req.toString() + "]");
		if (2 == req.getId()) {
			ctx.writeAndFlush(resp(req.getId()));
		}
	}

	private Message resp(long subReqID) {
		Message resp = new Message();
		resp.setId(subReqID);
		resp.setContent("客户端，我收到你的消息了！");
		return resp;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();// 发生异常，关闭链路
	}
}
