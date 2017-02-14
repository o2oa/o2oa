package com.x.base.core.gson;

import com.x.base.core.bean.PropertyObject;

public abstract class GsonPropertyObject extends PropertyObject {
	public String toString() {
		try {
			return XGsonBuilder.toJson(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
