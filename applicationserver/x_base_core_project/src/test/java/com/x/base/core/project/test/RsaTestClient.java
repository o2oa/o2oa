package com.x.base.core.project.test;

import org.junit.Test;

import com.x.base.core.project.tools.Crypto;

public class RsaTestClient {

	@Test
	public void test1() throws Exception {
		String aaa = "s3ZeJFvJVi5IHlMweNyqBOGxuGvuDBzV";
		System.out.println(Crypto.decrypt(aaa, "thisistheway"));
	}

}