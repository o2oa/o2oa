package com.x.base.core.project.test.tika;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.junit.Test;

public class TestClient {

	@Test
	public void test() throws Exception {
		Tika tika = new Tika();
		byte[] bs = FileUtils.readFileToByteArray(new File("e:/93ccd8d6-e699-4a82-a0b8-c4bd17e63dd8.doc"));
		System.out.println("!!!dd@@@!w!!!!!!1!! 1!!!!!!!!!!1!!!!");
		System.out.println(tika.detect(bs));
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

}
