package com.x.base.core.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.temporal.DateTimeTemporal;

public class DateTimeTemporalDeserializer implements JsonDeserializer<DateTimeTemporal> {
	public DateTimeTemporal deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		try {
			return new DateTimeTemporal(DateTools.parseDateTime(json.getAsJsonPrimitive().getAsString()).getTime());
		} catch (Exception e) {
			throw new JsonParseException(e.getMessage());
		}
	}
}