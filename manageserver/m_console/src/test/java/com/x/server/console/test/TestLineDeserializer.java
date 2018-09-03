package com.x.server.console.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestLineDeserializer {
	public static void main(String[] args) throws IOException, InterruptedException {

		String srcFilename = "D:/o2server/logs/2018_06_13.out.log";
		FileReader fr = new FileReader(srcFilename);
		BufferedReader bufr = new BufferedReader(fr);
		String line = null;
		while (true) {
			if ((line = bufr.readLine()) != null) {
				System.out.println(line);
				continue;
			}
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}