package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.correlation.core.entity.content.Correlation;

public class ActionCreateTypeProcessPlatformWo extends GsonPropertyObject {

	private static final long serialVersionUID = 8792811593252273112L;

	private List<Correlation> successList = new ArrayList<>();
	private List<TargetWo> failureList = new ArrayList<>();

	public List<Correlation> getSuccessList() {
		return successList;
	}

	public void setSuccessList(List<Correlation> successList) {
		this.successList = successList;
	}

	public List<TargetWo> getFailureList() {
		return failureList;
	}

	public void setFailureList(List<TargetWo> failureList) {
		this.failureList = failureList;
	}


}