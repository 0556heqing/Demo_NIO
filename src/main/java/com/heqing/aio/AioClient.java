package com.heqing.aio;

public class AioClient {

	public static void main(String[] args) {
		
		int port = 8080;
		new Thread(new AioClientHandler("127.0.0.1", port), "AIO-heqing-001").start();
	}
}
