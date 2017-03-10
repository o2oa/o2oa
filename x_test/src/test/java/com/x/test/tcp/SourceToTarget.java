package com.x.test.tcp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.Test;

public class SourceToTarget extends Thread {

	private Socket source;
	private Socket target;

	public SourceToTarget(Socket source, Socket target) throws Exception {
		this.source = source;
		this.target = target;
	}

	public void run() {
		try (InputStream sourceIn = source.getInputStream(); OutputStream targetOut = target.getOutputStream()) {
			int r;
			byte bs[] = new byte[4096];
			r = sourceIn.read(bs);
			while (r > -1 && (!source.isInputShutdown())) {
				System.out.println();
				System.out.println("SourceToTarget read:" + r + ",");
				try {
					targetOut.write(bs, 0, r);
					r = sourceIn.read(bs);
				} catch (Exception e) {
					break;
				}
			}
			System.out.println("SourceToTarget end loop.");
			source.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}