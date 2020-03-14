package com.x.base.core.project.gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	@Test
	public void test2() throws Exception {

		List<String> aaa = new ArrayList<>();
		aaa.add("abc");
		aaa.add("efg");
		aaa.add("hij");
		System.out.println(aaa);
	
		aaa.remove(0);
		for (String s : aaa) {
			System.out.println(s);
		}
	}

}
