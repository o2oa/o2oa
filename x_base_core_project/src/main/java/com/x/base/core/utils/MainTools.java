package com.x.base.core.utils;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class MainTools {

	public static <T> T parseArgument(String str, Class<T> clz) {
		String json = StringUtils.replace(str, "\\", "/");
		T t = XGsonBuilder.pureGsonDateFormated().fromJson(json, clz);
		return t;
	}

}
