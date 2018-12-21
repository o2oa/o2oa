package com.x.processplatform.assemble.surface.copier;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class TestClient {
	@Test
	public void test1() throws Exception {
		String aaa = "aaa";
		System.out.println(FilenameUtils.getName(aaa));
		System.out.println(FilenameUtils.getBaseName(aaa));
		System.out.println(FilenameUtils.getExtension(aaa));

	}

	@Test
	public void test3() throws Exception {
		char c = '\\';
		char s = '/';
		System.out.println((int) c);
		System.out.println((int) s);

	}

}
