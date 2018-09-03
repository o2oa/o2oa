package com.x.base.core.project.gson;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class LongDeserializer implements JsonDeserializer<Long> {

	@Override
	public Long deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		Number num = NumberUtils.createNumber(StringUtils.trimToNull(json.getAsString()));
		return (num == null) ? null : num.longValue();
	}

}