package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapCount extends GsonPropertyObject {

	public WrapCount() {

	}

	public WrapCount(Long o) {
		this.count = o;
	}

	public WrapCount(Integer o) {
		this.count = o.longValue();
	}

	@FieldDescribe("数量")
	private Long count;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
