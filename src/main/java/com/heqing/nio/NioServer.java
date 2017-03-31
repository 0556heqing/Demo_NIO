package com.heqing.nio;

import java.io.IOException;

public class NioServer {

	public static void main(String[] args) throws IOException {
		int port = 8080;

		MultiplexerNioServer timeServer = new MultiplexerNioServer(port);
		new Thread(timeServer, "NIO-heqing").start();
	}
}
