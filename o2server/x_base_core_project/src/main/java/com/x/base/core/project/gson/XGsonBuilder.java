package com.x.base.core.project.gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.x.base.core.project.tools.DateTools;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class XGsonBuilder {

	private static Gson INSTANCE;
	private static Gson COMPACTINSTANCE;

	public static Gson instance() {
		if (null == INSTANCE) {
			synchronized (XGsonBuilder.class) {
				if (null == INSTANCE) {
					GsonBuilder gson = new GsonBuilder();
					gson.setDateFormat(DateTools.format_yyyyMMddHHmmss);
					gson.registerTypeAdapter(Integer.class, new IntegerDeserializer());
					gson.registerTypeAdapter(Double.class, new DoubleDeserializer());
					gson.registerTypeAdapter(Float.class, new FloatDeserializer());
					gson.registerTypeAdapter(Long.class, new LongDeserializer());
					gson.registerTypeAdapter(Date.class, new DateDeserializer());
					gson.registerTypeAdapter(Date.class, new DateSerializer());
					gson.registerTypeAdapter(ScriptObjectMirror.class, new ScriptObjectMirrorSerializer());
					INSTANCE = gson.setPrettyPrinting().create();
				}
			}
		}
		return INSTANCE;
	}

	public static Gson compactInstance() {
		if (null == COMPACTINSTANCE) {
			synchronized (XGsonBuilder.class) {
				if (null == COMPACTINSTANCE) {
					GsonBuilder gson = new GsonBuilder();
					gson.setDateFormat(DateTools.format_yyyyMMddHHmmss);
					gson.registerTypeAdapter(Integer.class, new IntegerDeserializer());
					gson.registerTypeAdapter(Double.class, new DoubleDeserializer());
					gson.registerTypeAdapter(Float.class, new FloatDeserializer());
					gson.registerTypeAdapter(Long.class, new LongDeserializer());
					gson.registerTypeAdapter(Date.class, new DateDeserializer());
					gson.registerTypeAdapter(Date.class, new DateSerializer());
					COMPACTINSTANCE = gson.create();
				}
			}
		}
		return COMPACTINSTANCE;
	}

	public static <T> T convert(Object o, Class<T> cls) {
		if (null == o) {
			return null;
		}
		return instance().fromJson(instance().toJson(o), cls);
	}

	public static String toJson(Object o) {
		return instance().toJson(o);
	}

	public static String toText(Object o) {
		return instance().toJsonTree(o).toString();
	}

	public static String extractString(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element && element.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
				if (jsonPrimitive.isString())
					return jsonPrimitive.getAsString();
			}
		}
		return null;
	}

	public static Boolean extractBoolean(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element && element.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
				if (jsonPrimitive.isBoolean())
					return jsonPrimitive.getAsBoolean();
			}
		}
		return null;
	}

	public static List<String> extractStringList(JsonElement jsonElement, String name) {
		List<String> list = new ArrayList<>();
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element) {
				if (element.isJsonPrimitive()) {
					JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
					if (jsonPrimitive.isString()) {
						list.add(jsonPrimitive.getAsString());
					}
				} else if (element.isJsonArray()) {
					JsonArray jsonArray = element.getAsJsonArray();
					jsonArray.forEach(o -> {
						if ((null != o) && o.isJsonPrimitive()) {
							JsonPrimitive jsonPrimitive = o.getAsJsonPrimitive();
							if (jsonPrimitive.isString()) {
								list.add(jsonPrimitive.getAsString());
							}
						}
					});
				}
			}
		}
		return list;
	}

	public static JsonElement extract(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (StringUtils.contains(name, ".")) {
				String prefix = StringUtils.substringBefore(name, ".");
				String surfix = StringUtils.substringAfter(name, ".");
				if (jsonObject.has(prefix)) {
					return extract(jsonObject.get(prefix), surfix);
				}
			} else {
				if (jsonObject.has(name)) {
					return jsonObject.get(name);
				}
			}
		}
		return null;
	}

	public static <T> T extract(JsonElement jsonElement, String name, Class<T> cls, T defaultValue) {
		JsonElement element = extract(jsonElement, name);
		if (element == null || element.isJsonNull()) {
			return defaultValue;
		}
		return instance().fromJson(element, cls);
	}

	public static boolean isJsonObject(String json) {
		if (StringUtils.isBlank(json)) {
			return false;
		}
		try {
			JsonElement jsonElement = new JsonParser().parse(json);
			if (jsonElement.isJsonObject()) {
				return true;
			} else {
				return false;
			}
		} catch (JsonParseException e) {
			return false;
		}
	}

	public static boolean isJsonArray(String json) {
		if (StringUtils.isBlank(json)) {
			return false;
		}
		try {
			JsonElement jsonElement = new JsonParser().parse(json);
			if (jsonElement.isJsonArray()) {
				return true;
			} else {
				return false;
			}
		} catch (JsonParseException e) {
			return false;
		}
	}

	public static JsonElement merge(JsonElement from, JsonElement to) throws Exception {
		if (from == null) {
			throw new Exception("from jsonElement can't be null.");
		}
		if (to == null) {
			throw new Exception("to jsonElement can't be null.");
		}
		if (!from.isJsonObject()) {
			throw new Exception("from jsonElement must be a jsonObject.");
		}
		if (!to.isJsonObject()) {
			throw new Exception("to jsonElement must be a jsonObject.");
		}
		return merge(from.getAsJsonObject(), to.deepCopy().getAsJsonObject());
	}

	private static JsonObject merge(JsonObject from, JsonObject to) {
		for (Map.Entry<String, JsonElement> fromEntry : from.entrySet()) {
			String key = fromEntry.getKey();
			JsonElement fromValue = fromEntry.getValue();
			if (to.has(key)) {
				JsonElement toValue = to.get(key);
				if ((!fromValue.isJsonObject()) || (!toValue.isJsonObject())) {
					to.add(key, fromValue);
				} else {
					merge(fromValue.getAsJsonObject(), toValue.getAsJsonObject());
				}
			} else {
				to.add(key, fromValue);
			}
		}
		return to;
	}

}