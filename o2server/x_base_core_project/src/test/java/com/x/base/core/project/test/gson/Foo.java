package com.x.base.core.project.test.gson;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import com.google.gson.annotations.SerializedName;

public class Foo {

	@SerializedName("bbb")
	public String getBbb() {
		return bbb + "ttt";
	}

	public String aaa = "aaa";

	private String bbb = "bbb";

	public String getAaa() {
		return aaa;
	}

	public void setAaa(String aaa) {
		this.aaa = aaa;
	}

	public void setBbb(String bbb) {
		this.bbb = bbb;
	}

	@Test
	public void test() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Foo foo = new Foo();
		System.out.println(PropertyUtils.isReadable(foo, "aaaaa"));
		//System.out.println(PropertyUtils.getProperty(foo, "aaaaa"));

	}

}
