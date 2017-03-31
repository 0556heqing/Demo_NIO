package com.heqing.netty.protocol.plivate.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.heqing.netty.protocol.plivate.struct.Header;
import com.heqing.netty.protocol.plivate.struct.NettyMessage;

public class TestCodeC {

	MarshallingEncoder marshallingEncoder;
	MarshallingDecoder marshallingDecoder;

	public TestCodeC() throws IOException {
		marshallingDecoder = new MarshallingDecoder();
		marshallingEncoder = new MarshallingEncoder();
	}

	public NettyMessage getMessage() {
		NettyMessage nettyMessage = new NettyMessage();
		Header header = new Header();
		header.setLength(123);
		header.setSessionID(99999);
		header.setType((byte) 1);
		header.setPriority((byte) 7);
		Map<String, Object> attachment = new HashMap<String, Object>();
		for (int i = 0; i < 3; i++) {
			attachment.put("name" + i, "heqing" + i);
		}
		header.setAttachment(attachment);
		nettyMessage.setHeader(header);
		nettyMessage.setBody("----->Hello World!");
		return nettyMessage;
	}

	public ByteBuf encode(NettyMessage msg) throws Exception {
		ByteBuf sendBuf = Unpooled.buffer();
		sendBuf.writeInt((msg.getHeader().getCrcCode()));
		sendBuf.writeInt((msg.getHeader().getLength()));
		sendBuf.writeLong((msg.getHeader().getSessionID()));
		sendBuf.writeByte((msg.getHeader().getType()));
		sendBuf.writeByte((msg.getHeader().getPriority()));
		sendBuf.writeInt((msg.getHeader().getAttachment().size()));

		for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
			byte[] keyArray = param.getKey().getBytes("UTF-8");
			sendBuf.writeInt(keyArray.length);
			sendBuf.writeBytes(keyArray);
			Object value = param.getValue();
			marshallingEncoder.encode(value, sendBuf);
		}

		if (msg.getBody() != null) {
			marshallingEncoder.encode(msg.getBody(), sendBuf);
		} else
			sendBuf.writeInt(0);
		sendBuf.setInt(4, sendBuf.readableBytes());
		return sendBuf;
	}

	public NettyMessage decode(ByteBuf in) throws Exception {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setCrcCode(in.readInt());
		header.setLength(in.readInt());
		header.setSessionID(in.readLong());
		header.setType(in.readByte());
		header.setPriority(in.readByte());

		int size = in.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<String, Object>(size);
			for (int i = 0; i < size; i++) {
				int keySize = in.readInt();
				byte[] keyArray = new byte[keySize];
				in.readBytes(keyArray);
				String key = new String(keyArray, "UTF-8");
				attch.put(key, marshallingDecoder.decode(in));
			}
			header.setAttachment(attch);
		}
		if (in.readableBytes() > 4) {
			message.setBody(marshallingDecoder.decode(in));
		}
		message.setHeader(header);
		return message;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestCodeC testC = new TestCodeC();
		NettyMessage message = testC.getMessage();
		System.out.println(message + "[body="+message.getBody()+"]");
		
		System.out.println("-------------------------------------------------");
		
		ByteBuf buf = testC.encode(message);
		NettyMessage decodeMsg = testC.decode(buf);
		System.out.println(decodeMsg + "[body="+message.getBody()+"]");
			
	}

}
