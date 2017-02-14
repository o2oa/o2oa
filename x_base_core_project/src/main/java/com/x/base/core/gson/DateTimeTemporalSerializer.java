package com.x.base.core.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.temporal.DateTimeTemporal;

public class DateTimeTemporalSerializer implements JsonSerializer<DateTimeTemporal> {
	public JsonElement serialize(DateTimeTemporal temporal, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(DateTools.format(temporal));
	}
}
