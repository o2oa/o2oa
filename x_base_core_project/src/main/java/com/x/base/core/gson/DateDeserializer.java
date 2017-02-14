package com.x.base.core.gson;

import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.x.base.core.utils.DateTools;

public class DateDeserializer implements JsonDeserializer<Date> {
	public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		try {
			String text = json.getAsJsonPrimitive().getAsString();
			if (StringUtils.isNotEmpty(text)) {
				return DateTools.parseDateTime(text);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new JsonParseException(e.getMessage());
		}
	}
}