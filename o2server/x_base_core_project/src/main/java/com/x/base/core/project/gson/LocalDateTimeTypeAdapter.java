package com.x.base.core.project.gson;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.x.base.core.project.tools.DateTools;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTools.format_yyyyMMddHHmmss);

	@Override
	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonPrimitive()) {
			String text = json.getAsJsonPrimitive().getAsString();
			if (StringUtils.isNotEmpty(text)) {
				return LocalDateTime.parse(json.getAsString(), formatter);
			}
		}
		return null;
	}

	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
		if (null == src) {
			return JsonNull.INSTANCE;
		}
		return new JsonPrimitive(src.format(formatter));
	}
}