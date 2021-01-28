package com.x.base.core.entity;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class JsonProperties extends GsonPropertyObject {

	private static final long serialVersionUID = -5074100033957236455L;

	// properties的泛型在前后都必须申明类型!!!!LinkedHashMap<String, String>()这样的写法是对的.
	// properties的泛型在前后都必须申明类型!!!!LinkedHashMap<String, String>()这样的写法是对的.
	// properties的泛型在前后都必须申明类型!!!!LinkedHashMap<String, String>()这样的写法是对的.
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof JsonProperties)) {
			return false;
		} else {
			return StringUtils.equals(this.toString(), o.toString());
		}
	}
}
