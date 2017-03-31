package com.heqing.nio;

public class NioClient {

    public static void main(String[] args) {

		int port = 8080;
		new Thread(new NioClientHandle("127.0.0.1", port), "NIO-heqing-001").start();
    }
}
