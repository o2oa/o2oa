package com.x.base.core.project.test.gson;

import java.util.Date;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient {
	@Test
	public void test() throws Exception {
		String aaa = "{'aaa':'bbbddd','a1':['11','22','33'],'a2':['a','b','c'],'c':{'1':'11','2':'22','3':'33'}}";
		String bbb = "{'aaa':'bbb','a2':['a','b','e','f'],'c':{'4':'44','5':'55'}}";
		JsonElement a = XGsonBuilder.instance().fromJson(aaa, JsonElement.class);
		JsonElement b = XGsonBuilder.instance().fromJson(bbb, JsonElement.class);
		System.out.println(XGsonBuilder.merge(a, b));
	}
	@Test
	public void test1() throws Exception {
		Date date = new Date();
		System.out.println(date.getTime());
	}
}
