package com.x.processplatform.core.express.service.processing.jaxrs.job;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionExecutorStatusWo extends GsonPropertyObject {

	private static final long serialVersionUID = 8851351880636501257L;

	@FieldDescribe("是否繁忙")
	private Boolean busy;
	@FieldDescribe("队列长度")
	private Integer size;

	public Boolean getBusy() {
		return busy;
	}

	public void setBusy(Boolean busy) {
		this.busy = busy;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}