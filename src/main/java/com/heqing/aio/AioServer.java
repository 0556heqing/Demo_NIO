package com.heqing.aio;

import java.io.IOException;

public class AioServer {

	public static void main(String[] args) throws IOException {
		int port = 8080;
		AioServerHandler timeServer = new AioServerHandler(port);
		new Thread(timeServer, "AIO-heqing").start();
	}
}
