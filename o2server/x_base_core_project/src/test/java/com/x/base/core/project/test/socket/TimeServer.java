package com.x.base.core.project.test.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class TimeServer {

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(10000)) {
			System.out.println("Server is listening on port " + 10000);

			while (true) {
				Socket socket = serverSocket.accept();
				socket.setKeepAlive(true);

				DataInputStream input = new DataInputStream(socket.getInputStream());
				System.out.println(input.readUTF());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				output.writeUTF("aaaaabbbbdd");

			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}