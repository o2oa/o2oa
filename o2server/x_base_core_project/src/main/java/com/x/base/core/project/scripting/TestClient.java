package com.x.base.core.project.scripting;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient {
	public static void main(String[] args) {

		String text = "\"1234\"";
		Integer n = 5;
		Boolean b = true;
		Map<String, String> a = new HashMap<>();
		a.put("ddd", "ccc");

		// String abc = XGsonBuilder.toJson(a);

		// System.out.println(abc);

		JsonElement js = XGsonBuilder.instance().toJsonTree(text);

		System.out.println(js);
//
		System.out.println(js.isJsonPrimitive() + "!!");

		System.out.println(js.getAsJsonPrimitive().isString());

		System.out.println(js.getAsJsonPrimitive().getAsJsonPrimitive().isString());

		System.out.println(js.getAsString());

	}

}
