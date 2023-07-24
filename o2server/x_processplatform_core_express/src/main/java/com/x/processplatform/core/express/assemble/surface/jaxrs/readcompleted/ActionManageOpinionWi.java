package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageOpinionWi extends GsonPropertyObject {

	private static final long serialVersionUID = 3139635911900728990L;

	@FieldDescribe("意见.")
	@Schema(description = "意见.")
	private String opinion;

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}