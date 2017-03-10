package com.x.base.core.project.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.ListTools;

public class TestList {
	@Test
	public void test() {
		List<String> a = new ArrayList<>();
		a.add("AAAAAAAAA");
		a.add("cc");
		a.add("bb");
		System.out.println(ListTools.isEmpty(a));
		System.out.println(ListTools.isNotEmpty(a));
		List<String> b = new ArrayList<>();
		List<String> c = new ArrayList<>();
		System.out.println(ListTools.isEmpty(b, c));
		System.out.println(ListTools.isNotEmpty(b, c));
	}

	@Test
	public void test1() {
		List<String> a = new ArrayList<>();
		a.add("AAAAAAAAA");
		a.add("cc");
		a.add("bb");
		System.out.println(ListTools.isEmpty(a));
		System.out.println(ListTools.isNotEmpty(a));
		List<String> b = new ArrayList<>();
		List<String> c = new ArrayList<>();
		System.out.println(ListTools.isEmpty(b, c));
		System.out.println(ListTools.isNotEmpty(b, c));
	}

	@Test
	public void test4() throws Exception{
		List<String> a = new ArrayList<>();
		a.add("a");
		a.add("b");
		a.add("c");
		a.add("d");
		a.add("e");
		a.add("f");
		a.add("g");
		System.out.println(XGsonBuilder.toJson(ListTools.batch(a, 1)));
	}

}
