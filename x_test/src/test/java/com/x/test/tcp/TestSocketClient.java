package com.x.test.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.junit.Test;

public class TestSocketClient {

	public static void main(String[] args) throws Exception {
		try (ServerSocket server = new ServerSocket(30000)) {
			Socket source = null;
			while (true) {
				// 等待客户端的连接，如果没有获取连接
				source = server.accept();
				System.out.println("与客户端连接成功！");
				// SocketTransfer socketTransfer = new SocketTransfer(source);
				try {
					Socket target = new Socket("127.0.0.1", 80);
				Thread targetToSource = new TargetToSource(source, target);
				Thread sourceToTarget = new SourceToTarget(source, target);
				targetToSource.start();
				sourceToTarget.start();
				// socketTransfer.start();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public void intToByteArray() {
		int a = -1;
		byte[] bs = new byte[] { (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
				(byte) (a & 0xFF) };
		System.out.println(Arrays.toString(bs));
	}
}
