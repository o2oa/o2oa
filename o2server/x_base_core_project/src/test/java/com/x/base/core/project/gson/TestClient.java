package com.x.base.core.project.gson;

import java.util.Date;

import org.junit.Test;

import com.x.base.core.project.tools.Crypto;

public class TestClient {
	@Test
	public void test() throws Exception {
		System.out.println(Crypto.encrypt("张学良#" + (new Date()).getTime(), "12345678"));
	}
	
	@Test
	public void test1() throws Exception {
		System.out.println(String.class.getTypeName());
	}


}
