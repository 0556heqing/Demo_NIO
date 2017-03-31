package com.heqing.pio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PioServer {

	public static void main(String[] args) throws IOException {
		int port = 8080;
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port : " + port);
			Socket socket = null;
			PioServerHandlerExecutePool singleExecutor = new PioServerHandlerExecutePool(50, 10000);// 创建IO任务线程池
			while (true) {
				socket = server.accept();
				singleExecutor.execute(new PioServerHandler(socket));
			}
		} finally {
			if (server != null) {
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
	}
}
