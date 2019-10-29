package com.x.processplatform.core.processing.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.element.Projection;

public class TestClient {
	@Test
	public void test() throws Exception {
		List<String> list = new ArrayList<>();
		String aaa = list.stream().findFirst().orElse(null);
		System.out.println("aaa:" + aaa);
		System.out.println(list.stream().allMatch(o -> StringUtils.isEmpty(o)));
	}

	@Test
	public void test1() throws Exception {
		List<String> a = new ArrayList<>();
		List<String> b = new ArrayList<>();
		a.add("11");
		a.add("22");
		a.add("33");
		b.add("22");
		b.add("33");
		b.add("44");
		System.out.println(ListUtils.sum(a, b));
		System.out.println(ListUtils.intersection(a, b));
		System.out.println(ListUtils.union(a, b));
	}

	@Test
	public void test2() {
		List<String> aaa = new ArrayList<>();

		aaa.add("aaa");
		aaa.add("bbb");
		aaa.add("ccc");

		List<String> bbb = new ArrayList<>();

		bbb.add("111");
		bbb.add("222");

		List<List<String>> list = new ArrayList<>();

		list.add(aaa);
		list.add(bbb);

		list = list.stream().sorted((o1, o2) -> {
			return o2.size() - o1.size();
		}).collect(Collectors.toList());

		System.out.println(list);

	}
	
	@Test
	public void test3() {
		String aa="dddddddddd,aaa,bbb";
//System.out.println(StringUtils.sub(aa, "@"));
		
	}

}
