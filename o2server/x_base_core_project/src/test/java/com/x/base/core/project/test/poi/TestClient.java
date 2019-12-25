package com.x.base.core.project.test.poi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

public class TestClient {

	@Test
	public void test() throws Exception {

		byte[] bs = FileUtils.readFileToByteArray(new File("e:/1.html"));
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bs);
				FileOutputStream fos = new FileOutputStream("e:/1.docx")) {
			XWPFDocument docx = new XWPFDocument(bais);
			docx.write(fos);

		}

	}
}
