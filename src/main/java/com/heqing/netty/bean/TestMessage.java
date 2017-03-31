package com.heqing.netty.bean;

import com.google.protobuf.InvalidProtocolBufferException;
import com.heqing.netty.bean.protobuf.ProtobufProto;

public class TestMessage {

	private static byte[] encode(ProtobufProto.testBuf msg) {
		return msg.toByteArray();
	}
	
	private static ProtobufProto.testBuf decode(byte[] body) throws InvalidProtocolBufferException {
		return ProtobufProto.testBuf.parseFrom(body);
	}
	
	private static ProtobufProto.testBuf createMessage() {
		ProtobufProto.testBuf.Builder builder = ProtobufProto.testBuf.newBuilder();
		builder.setId(1l);
		builder.setReceive(10001l);
		builder.setSend(10002l);
		builder.setContent("this is test");
		builder.setType(0);
		builder.setState(0);
		builder.setCreateTime(System.currentTimeMillis());
		return builder.build();
	}
	
	public static void main(String[] args) {
		try {
			ProtobufProto.testBuf msg1 = createMessage();
			System.out.println("11--->"+msg1.toString());
			
			ProtobufProto.testBuf msg2 = decode(encode(msg1));
			System.out.println("22--->"+msg1.toString());
			
			System.out.println("33--->"+msg1.equals(msg2));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
