package com.x.organization.core.entity.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.x.base.core.project.gson.DateDeserializer;
import com.x.base.core.project.gson.DateSerializer;
import com.x.base.core.project.gson.DoubleDeserializer;
import com.x.base.core.project.gson.FloatDeserializer;
import com.x.base.core.project.gson.IntegerDeserializer;
import com.x.base.core.project.gson.LongDeserializer;
import com.x.base.core.project.organization.Person;

public class TestClient {

	@Test
	public void test() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		list.add("ddd");
		list.add("eee");
		System.out.println(list.subList(list.indexOf("bbb"), list.size()));
	}

}
