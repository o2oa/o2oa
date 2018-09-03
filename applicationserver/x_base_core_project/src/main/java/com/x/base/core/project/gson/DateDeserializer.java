package com.x.base.core.project.gson;

import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.x.base.core.project.tools.DateTools;

public class DateDeserializer implements JsonDeserializer<Date> {
	public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		try {
			String text = json.getAsJsonPrimitive().getAsString();
			if (StringUtils.isNotEmpty(text)) {
				return DateUtils.parseDate(text, DateTools.format_yyyyMMddHHmmss, DateTools.format_yyyyMMdd,
						DateTools.format_HHmmss, DateTools.formatCompact_yyyyMMddHHmmss,
						DateTools.formatCompact_yyyyMMdd, DateTools.formatCompact_HHmmss);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new JsonParseException(e.getMessage());
		}
	}
}