package com.heqing.netty.protocol.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

public class UDPServerHandler extends
		SimpleChannelInboundHandler<DatagramPacket> {

	private static final String[] DICTIONARY = {"Spring", "MyBatis", "Hibernate", "Struts", "Netty", "JMS"};

	private String nextQuote() {
		int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
		return DICTIONARY[quoteId];
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		String req = packet.content().toString(CharsetUtil.UTF_8);
		if ("Java".equals(req)) {
			ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(
					"-->" + nextQuote(), CharsetUtil.UTF_8), packet.sender()));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
	}
}
