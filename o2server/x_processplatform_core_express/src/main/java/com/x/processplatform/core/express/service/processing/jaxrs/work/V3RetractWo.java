package com.x.processplatform.core.express.service.processing.jaxrs.work;

import com.x.base.core.project.gson.GsonPropertyObject;

public class V3RetractWo extends GsonPropertyObject {

	private static final long serialVersionUID = 7748368868225775443L;

	private String work;
	
	private Boolean needToProcessing = false;

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public Boolean getNeedToProcessing() {
		return needToProcessing;
	}

	public void setNeedToProcessing(Boolean needToProcessing) {
		this.needToProcessing = needToProcessing;
	}

	
	
}