package com.x.base.core.project.gson;

import org.apache.commons.beanutils.PropertyUtils;

import com.x.base.core.project.bean.PropertyObject;

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
