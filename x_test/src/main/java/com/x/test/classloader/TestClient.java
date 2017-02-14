package com.x.test.classloader;

import org.junit.Test;

public class TestClient {

	@Test
	public void test() {
		ClassLoader cl = this.getClass().getClassLoader();
		while (cl != null) {
			System.out.println(cl);
			cl = cl.getParent();
		}
		System.out.println(ClassLoader.getSystemClassLoader());
	}

}