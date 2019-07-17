package com.x.server.console.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.server.console.CommandFactory;

public class TestClient {

	@Test
	public void test1() throws Exception {
		FileUtils.writeByteArrayToFile(new File("d:/111.png"), Base64.decodeBase64(CommandFactory.DEFAULT_STARTIMAGE));
	}

	@Test
	public void test2() throws Exception {
		File file = new File("d:/aaa");
		for (File f : file.listFiles()) {
			System.out.println(f.getAbsolutePath());
			if (f.getName().equals("b.txt")) {
				f.delete();
			}
		}
		for (File f : file.listFiles()) {
			System.out.println(f.getAbsolutePath());
		}
	}

}
