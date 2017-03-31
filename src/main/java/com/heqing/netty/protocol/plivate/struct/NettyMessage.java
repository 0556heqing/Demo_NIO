package com.heqing.netty.protocol.plivate.struct;

public final class NettyMessage {

	private Header header;		//消息头
	private Object body;		//消息体

	/**
	 * @return the header
	 */
	public final Header getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public final void setHeader(Header header) {
		this.header = header;
	}

	/**
	 * @return the body
	 */
	public final Object getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public final void setBody(Object body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "NettyMessage [header=" + header + "]";
	}
}
