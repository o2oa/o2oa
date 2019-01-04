package com.x.base.core.entity;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class StringValueMap extends LinkedHashMap<String, String> {

	private static final long serialVersionUID = -2383150323755835614L;

	public String getString(String key) {
		return this.get(key);
	}

	public String getString(String key, String defaultValue) {
		return this.getOrDefault(key, defaultValue);
	}

	public Double getDouble(String key) {
		String str = this.get(key);
		if (!NumberUtils.isParsable(str)) {
			return null;
		}
		return NumberUtils.toDouble(str);
	}

	public Double getDouble(String key, Double defaultValue) {
		String str = this.get(key);
		if (!NumberUtils.isParsable(str)) {
			return defaultValue;
		}
		return NumberUtils.toDouble(str);
	}

	public Integer getInteger(String key) {
		String str = this.get(key);
		if (!NumberUtils.isParsable(str)) {
			return null;
		}
		return NumberUtils.toInt(str);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		String str = this.get(key);
		if (!NumberUtils.isParsable(str)) {
			return defaultValue;
		}
		return NumberUtils.toInt(str);
	}

	public Long getLong(String key) {
		String str = this.get(key);
		if (!NumberUtils.isParsable(str)) {
			return null;
		}
		return NumberUtils.toLong(str);
	}

	public Long getLong(String key, Long defaultValue) {
		String str = this.get(key);
		if (!NumberUtils.isParsable(str)) {
			return defaultValue;
		}
		return NumberUtils.toLong(str);
	}

	public Boolean getBoolean(String key) {
		String str = this.get(key);
		return BooleanUtils.toBooleanObject(str);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		String str = this.get(key);
		if (StringUtils.isEmpty(str)) {
			return defaultValue;
		}
		return BooleanUtils.toBooleanObject(str);
	}
}
