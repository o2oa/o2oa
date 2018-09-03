package com.x.server.console.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.util.FileUtils;
import org.eclipse.jetty.http.MimeTypes;
import org.junit.Test;

import com.x.server.console.OptionFactory;

public class TestClient {
	@Test
	public void test() throws IOException {
		MimeTypes mimeTypes = new MimeTypes();
		mimeTypes.addMimeMapping("", "application/octet-stream");
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

	@Test
	public void test1() {
		try {
			File file = new File("d:/error.log");
			System.out.println(file.getName());
			System.out.println(FilenameUtils.getBaseName(file.getName()));
			File f1 = new File("d:/111/222/333/444/a.jpg");
			File f2 = new File("d:/aaa/bbb/b.jpg");
			System.out.println(FileUtils.getRelativePath(f1, f2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3() throws Exception {

		String value = "172,18,75,196,116,70";
		InetSocketAddress addr = org.apache.ftpserver.util.SocketAddressEncoder.decode(value);
		System.out.println(addr.getPort());

	}

}