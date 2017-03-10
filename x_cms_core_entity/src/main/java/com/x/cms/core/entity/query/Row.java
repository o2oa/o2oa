package com.x.cms.core.entity.query;

import java.util.LinkedHashMap;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.gson.GsonPropertyObject;

public class Row extends GsonPropertyObject {

	private String categoryId;

	private LinkedHashMap<String, Object> data;

	public Row(String categoryId) {
		this.categoryId = categoryId;
		data = new LinkedHashMap<String, Object>();
	}

	public Double getAsDouble(String key) {
		Object o = this.data.get(key);
		String val = Objects.toString(o, "");
		if (NumberUtils.isNumber(val)) {
			return NumberUtils.toDouble(val);
		} else {
			return 0d;
		}
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setData(LinkedHashMap<String, Object> data) {
		this.data = data;
	}

	public LinkedHashMap<String, Object> getData() {
		return data;
	}

	public Object get(String key) {
		return this.data.get(key);
	}

	public void put(String key, Object value) {
		data.put(key, value);
	}

}
