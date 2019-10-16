package com.x.processplatform.core.express.query;

import java.util.LinkedHashMap;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Row extends GsonPropertyObject {

	private String job;

	private LinkedHashMap<String, Object> data;

	public Row(String job) {
		this.job = job;
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

	public String getJob() {
		return job;
	}

	public LinkedHashMap<String, Object> getData() {
		return data;
	}

	public Object find(String key) {
		return this.data.get(key);
	}

	public void put(String key, Object value) {
		this.data.put(key, value);
	}

}
