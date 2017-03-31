package com.heqing.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    public static void main(String[] args) throws IOException {
    	ServerSocket server = null;
    	int port = 8080;
	
    	try {
		    server = new ServerSocket(port);
		    System.out.println("The time server is start in port : " + port);
		    Socket socket = null;
		    while (true) {
				socket = server.accept();
				new Thread(new BioServerHandler(socket)).start();
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
