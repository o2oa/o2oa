package com.x.processplatform.core.express.service.processing.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionAssignCreateWo extends GsonPropertyObject {

	private static final long serialVersionUID = 2912634603504823685L;

	@FieldDescribe("标识")
	private String id;

	@FieldDescribe("job")
	private String job;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

}