package com.x.base.core.project.test.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * This program demonstrates a simple TCP/IP socket client.
 *
 * @author www.codejava.net
 */
public class TimeClient {

	public static void main(String[] args) {

		try (Socket socket = new Socket("127.0.0.1", 10000)) {
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF("aaaaa");
			DataInputStream input = new DataInputStream(socket.getInputStream());

			String time = input.readUTF();

			System.out.println(time);

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}
}