package com.x.query.core.express.plan;

import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Row extends GsonPropertyObject {

	public String bundle;

	public TreeMap<String, Object> data;

	public Row(String bundle) {
		this.bundle = bundle;
		data = new TreeMap<String, Object>();
	}

	public Object find(String key) {
		return this.data.get(key);
	}

	public void put(String key, Object value) {
		this.data.put(key, value);
	}

	/** 统计计算时用于转换值,不可转换的值默认为0 */
	public Double getAsDouble(String key) {
		Object o = this.data.get(key);
		String val = Objects.toString(o, "");
		if (NumberUtils.isCreatable(val)) {
			return NumberUtils.toDouble(val);
		} else {
			return 0d;
		}
	}

}