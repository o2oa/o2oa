package com.x.program.center.test;

import org.junit.Test;

public class TestClient {

	@Test
	public void test7() throws Exception {
		System.out.println(A.sss);
		System.out.println(B.sss);
		B.sss = "fffff";
		System.out.println(A.sss);
		System.out.println(B.sss);
	}
}
