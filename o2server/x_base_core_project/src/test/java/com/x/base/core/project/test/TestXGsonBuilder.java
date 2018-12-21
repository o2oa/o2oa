package com.x.base.core.project.test;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestXGsonBuilder {
	@Test
	public void test() throws Exception {
		String str = "{\"city\": {\"name\":\"杭州\",\"address\":\"wenerlu\"},\"name\":\"字典\"}";
		JsonElement json = XGsonBuilder.instance().fromJson(str, JsonElement.class);
		System.out.println(json);
		System.out.println(XGsonBuilder.extractString(json, "name"));
		System.out.println(XGsonBuilder.extractString(json, "city.name"));
		System.out.println(XGsonBuilder.extractString(json, "city.we"));
		System.out.println(XGsonBuilder.extractString(json, "city.name.name"));
		System.out.println(XGsonBuilder.extractString(json, "city"));
	}

}
