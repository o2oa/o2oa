package com.x.test.tcp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TargetToSource extends Thread {

	private Socket source;
	private Socket target;

	public TargetToSource(Socket source, Socket target) throws Exception {
		this.source = source;
		this.target = target;
	}

	public void run() {
		try (InputStream targetIn = target.getInputStream(); OutputStream sourceOut = source.getOutputStream()) {
			int r;
			byte bs[] = new byte[4096];
			r = targetIn.read(bs);
			while (r > -1 && (!target.isInputShutdown())) {
				System.out.println("TargetToSource read:" + r);
				try {
					sourceOut.write(bs, 0, r);
					r = targetIn.read(bs);
				} catch (Exception e) {
					break;
				}
			}
			System.out.println("TargetToSource end loop.");
			target.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}