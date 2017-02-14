package com.x.base.core.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.temporal.TimeTemporal;

public class TimeTemporalSerializer implements JsonSerializer<TimeTemporal> {
	public JsonElement serialize(TimeTemporal temporal, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(DateTools.formatTime(temporal));
	}
}
