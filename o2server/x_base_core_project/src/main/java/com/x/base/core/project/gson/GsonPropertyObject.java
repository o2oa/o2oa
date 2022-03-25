package com.x.base.core.project.gson;

import com.x.base.core.project.bean.PropertyObject;

public abstract class GsonPropertyObject extends PropertyObject {

	private static final long serialVersionUID = 2965040749541469280L;

	public String toString() {
		try {
			return XGsonBuilder.toJson(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
