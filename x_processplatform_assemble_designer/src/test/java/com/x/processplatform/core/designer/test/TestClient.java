package com.x.processplatform.core.designer.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestClient {
	@Test
	public void test() throws Exception {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9\\_\\(\\)\\-\\.]*$");
		Matcher matcher = pattern.matcher("aaa");
		System.out.println(matcher.find());
	}
}
