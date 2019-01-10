package com.x.program.center.test;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

public class TestClient {

	@Test
	public void test() {
		String name = "张三丰";
		System.out.println(name.substring(0, 1));
		System.out.println(name.substring(1));
	}

	@Test
	public void test1() {
		String name = "张三丰";
		System.out.println(StringEscapeUtils.escapeJava(name));
	}

}
