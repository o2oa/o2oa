package com.x.base.core.http;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutCount extends GsonPropertyObject {

	public WrapOutCount(Long count) throws Exception {
		this.count = count;
	}

	public WrapOutCount(Integer count) throws Exception {
		this.count = count.longValue();
	}

	public WrapOutCount() {
	}

	private Long count;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public void setCount(Integer count) {
		this.count = count.longValue();
	}

}
