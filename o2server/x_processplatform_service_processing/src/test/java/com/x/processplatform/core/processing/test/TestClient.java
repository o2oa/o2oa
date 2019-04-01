package com.x.processplatform.core.processing.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.gson.XGsonBuilder;

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
		System.out.println(ListUtils.sum(a,b));
	}
	
	@Test
	public void test2() throws Exception {
		Map<String,String> a = new HashMap<>();
		a.put("aaaa", "a1");
		a.put("bbbb", "b1");
		a.put("cccc", "c1");
		JsonElement je = XGsonBuilder.instance().toJsonTree(a);
		JsonObject jo = je.getAsJsonObject();
		jo.addProperty("bbbb", "xxxxx");
		jo.addProperty("ddddd", "xxxxx");
		System.out.println(jo);
	}

}
