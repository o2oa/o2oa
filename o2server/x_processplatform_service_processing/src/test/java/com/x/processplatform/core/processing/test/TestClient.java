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
	}



}
