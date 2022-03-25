package com.x.base.core.project.gson;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DoubleDeserializer implements JsonDeserializer<Double> {

	@Override
	public Double deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		/*Number num = NumberUtils.createNumber(StringUtils.trimToNull(json.getAsString()));
		return (num == null) ? null : num.doubleValue();*/
		if(StringUtils.isBlank(json.getAsString())){
			return null;
		}else{
			return Double.parseDouble(StringUtils.trim(json.getAsString()));
		}
	}

}
