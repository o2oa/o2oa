package com.x.test.password;

import org.junit.Test;

import com.wx.pwd.CheckStrength;

public class TestClient {

	@Test
	public void test1() {
		String passwd = "hello123";
		System.out.println(CheckStrength.checkPasswordStrength(passwd));
	}
}
