package com.x.base.core.gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.temporal.DateTemporal;
import com.x.base.core.utils.temporal.DateTimeTemporal;
import com.x.base.core.utils.temporal.TimeTemporal;

public class XGsonBuilder {

	public static Gson instance() {
		GsonBuilder gson = new GsonBuilder();
		gson.setDateFormat(DateTools.format_yyyyMMddHHmmss);
		gson.registerTypeAdapter(DateTimeTemporal.class, new DateTimeTemporalSerializer());
		gson.registerTypeAdapter(DateTimeTemporal.class, new DateTimeTemporalDeserializer());
		gson.registerTypeAdapter(DateTemporal.class, new DateTemporalSerializer());
		gson.registerTypeAdapter(DateTemporal.class, new DateTemporalDeserializer());
		gson.registerTypeAdapter(TimeTemporal.class, new TimeTemporalSerializer());
		gson.registerTypeAdapter(TimeTemporal.class, new TimeTemporalDeserializer());
		gson.registerTypeAdapter(Integer.class, new IntegerDeserializer());
		gson.registerTypeAdapter(Double.class, new DoubleDeserializer());
		gson.registerTypeAdapter(Float.class, new FloatDeserializer());
		gson.registerTypeAdapter(Long.class, new LongDeserializer());
		/* Date 类型仅注册反序列化 */
		gson.registerTypeAdapter(Date.class, new DateDeserializer());
		return gson.setPrettyPrinting().create();
	}

	public static Gson pureGsonDateFormated() {
		GsonBuilder gson = new GsonBuilder();
		gson.setDateFormat(DateTools.format_yyyyMMddHHmmss);
		return gson.setPrettyPrinting().create();
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

	// public static String extractStringField(JsonElement jsonElement, String
	// name) {
	// if ((null != jsonElement) && jsonElement.isJsonObject()) {
	// JsonObject jsonObject = jsonElement.getAsJsonObject();
	// if (jsonObject.has(name)) {
	// return jsonObject.get(name).getAsString();
	// }
	// }
	// return null;
	// }

}