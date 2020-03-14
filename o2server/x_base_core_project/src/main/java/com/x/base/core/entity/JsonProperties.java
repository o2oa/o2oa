package com.x.base.core.entity;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class JsonProperties extends GsonPropertyObject {

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
