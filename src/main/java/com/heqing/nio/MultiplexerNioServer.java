package com.heqing.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerNioServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel servChannel;
	private volatile boolean stop;

	/**
	 * 初始化多路复用器、绑定监听端口
	 * 
	 * @param port
	 */
	public MultiplexerNioServer(int port) {
		try {
			//1.创建多路复用器
			selector = Selector.open();
			//2.用于监听客户端的链接，所有客户端连接的父管道
			servChannel = ServerSocketChannel.open();
			//3.绑定端口，设置为非阻塞式
			servChannel.socket().bind(new InetSocketAddress(port), 1024);
			servChannel.configureBlocking(false);
			//4.将ServerSocketChannel绑定到多路复用器，并监听ACCEPT事件
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port : " + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void stop() {
		this.stop = true;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				//5.多路复用器轮询准备好的KEY
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null)
								key.channel().close();
						}
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		// 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
		if (selector != null)
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	// 处理新接入的请求消息
	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			if (key.isAcceptable()) {
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				//6.多路复用器监听到新的客户端链接，完成3次握手，建立物理路由，处理新请求
				SocketChannel sc = ssc.accept();
				//7.设置客户端路由为非阻塞式
				sc.configureBlocking(false);
				//8.将新接入的客户端注册到多路由上，监听操作，读取客户端传递来的数据
				sc.register(selector, SelectionKey.OP_READ);
			}
			if (key.isReadable()) {
				// Read the data
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				//9.异步读取客户端传递数据放入到缓冲区
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					//10.处理消息文件
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order : "+ body);
					String currentTime = "heqing".equalsIgnoreCase(body) ? 
							new java.util.Date(System.currentTimeMillis()).toString(): "BAD ORDER";
					doWrite(sc, currentTime);
				} else if (readBytes < 0) {
					// 对端链路关闭
					key.cancel();
					sc.close();
				} 
				// 读到0字节，忽略
			}
		}
	}

	private void doWrite(SocketChannel channel, String response)
			throws IOException {
		if (response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			//11.将返回信息转换为byteBuffer，调用SocketChannel的异步write接口，将消息异步发送到客户端
			channel.write(writeBuffer);
		}
	}
}
