package com.x.base.core.gson;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

	public static String toJson(Object o) throws Exception {
		return instance().toJson(o);
	}

	public static String extractStringField(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has(name)) {
				return jsonObject.get(name).getAsString();
			}
		}
		return null;
	}

}