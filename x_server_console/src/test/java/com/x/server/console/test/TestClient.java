package com.x.server.console.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.http.MimeTypes;
import org.junit.Test;

import com.x.server.console.OptionFactory;

public class TestClient {
	@Test
	public void test() throws IOException {
		MimeTypes mimeTypes = new MimeTypes();
		mimeTypes.addMimeMapping("", "application/octet-stream");
		System.out.println(mimeTypes.getMimeByExtension("aaa.jpg"));
		System.out.println("!!!!!!!!!!!!!!!1");
		System.out.println(mimeTypes.getMimeByExtension("aaa."));
	}

	@Test
	public void test2() throws Exception {
		MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
		System.out.println(mimeTypes.getContentType(""));

	}

	@Test
	public void basePath() {
		try {
			Options options = OptionFactory.options();
			HelpFormatter formatter = new HelpFormatter();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			formatter.printHelp("O2Platform", options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}