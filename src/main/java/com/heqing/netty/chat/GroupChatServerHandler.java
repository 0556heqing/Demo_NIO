package com.heqing.netty.chat;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<Object> {

	private WebSocketServerHandshaker handshaker;
	
	private final ChannelGroup group;
	
	public GroupChatServerHandler(ChannelGroup group) {
		super();
		this.group = group;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 传统的HTTP接入
		if (msg instanceof FullHttpRequest) handleHttpRequest(ctx, (FullHttpRequest) msg);
		// WebSocket接入
		else if (msg instanceof WebSocketFrame) handleWebSocketFrame(ctx, (WebSocketFrame) msg);
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		// 如果HTTP解码失败，返回HHTP异常
		if (!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
			FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
			// 返回应答给客户端
			if (res.getStatus().code() != 200) {
				ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
				res.content().writeBytes(buf);
				buf.release();
				setContentLength(res, res.content().readableBytes());
			} 

			// 如果是非Keep-Alive，关闭连接
			ChannelFuture f = ctx.channel().writeAndFlush(res);
			if (!isKeepAlive(req) || res.getStatus().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
			return;
		}

		// 构造握手响应返回，本机测试
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// 判断是否是关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// 判断是否是Ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		// 本例程仅支持文本消息，不支持二进制消息
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}
		String msg = ctx.channel().id()+" --> "+((TextWebSocketFrame) frame).text();
		group.writeAndFlush(new TextWebSocketFrame(msg));
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		group.add(ctx.channel());
		group.writeAndFlush(new TextWebSocketFrame("Client "+ctx.channel()+" joined!"));
	}
	
	@Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception { 
		group.remove(ctx.channel());
		group.writeAndFlush(new TextWebSocketFrame("Client "+ctx.channel()+" remove!"));
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		group.remove(ctx.channel());
		group.writeAndFlush(new TextWebSocketFrame("Client "+ctx.channel()+" lose!"));
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}