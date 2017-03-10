package com.x.test.tcp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTransfer extends Thread {

	private Socket client;

	public SocketTransfer(Socket client) throws Exception {
		this.client = client;
	}

	public void run() {
		try (InputStream sourceIn = client.getInputStream();
				OutputStream sourceOut = client.getOutputStream();
				Socket target = new Socket("127.0.0.1", 20050);
				InputStream targetIn = target.getInputStream();
				OutputStream targetOut = target.getOutputStream()) {
			int b;
			while (client.isConnected() && target.isConnected()) {
				b = sourceIn.read();
				while (b > -1) {
					System.out.println("sourceIn read:" + b);
					targetOut.write(b);
					b = sourceIn.read();
				}
				targetOut.flush();
				b = targetIn.read();
				while (b > -1) {
					System.out.println("targetIn read:" + b);
					sourceOut.write(b);
					b = targetIn.read();
				}
				sourceOut.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}