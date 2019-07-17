package com.x.base.core.project.test.gson;

import org.junit.Test;

import com.google.gson.Gson;

public class TestClient {

	@Test
	public void test() {
		Foo foo = new Foo();
		Gson gson = new Gson();
		System.out.println(gson.toJson(foo));

	}

	@Test
	public void test2() {
		String value = "{\"aaa\":\"aaa\",\"bbb\":\"bbb\"}";
		Gson gson = new Gson();
		Foo foo = gson.fromJson(value, Foo.class);
		System.out.println(foo.aaa);
		//System.out.println(foo.bbb);
	}
}
