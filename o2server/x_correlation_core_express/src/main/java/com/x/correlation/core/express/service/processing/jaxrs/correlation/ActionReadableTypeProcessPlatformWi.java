package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionReadableTypeProcessPlatformWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4094330843553910855L;

	private String person;

	private String job;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

}