package com.x.test.encode;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestClient {

	@Test
	public void test() throws Exception {
		File file = new File(
				"D:/x/describe/javadoc/x_processplatform_service_processing/com/x/processplatform/core/processing/jaxrs/review/ReviewAction.html");
		String str = FileUtils.readFileToString(file);
		byte[] bs = str.getBytes("utf-8");
		FileUtils.writeStringToFile(file, new String(bs, "utf-8"), "utf-8");
	}

	@Test
	public void test1() throws Exception {
		String s1 = "\u5e7f\u5c9b\u4e4b\u604b.mp3";
		byte[] converttoBytes = s1.getBytes("UTF-8");
		String s2 = new String(converttoBytes, "UTF-8");
		System.out.println(s2);
	}
}