package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2ResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	@FieldDescribe("保留自身待办.")
	private Boolean keep;

	@FieldDescribe("重置身份")
	private List<String> identityList;

	public Boolean getKeep() {
		return BooleanUtils.isTrue(keep);
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public void setKeep(Boolean keep) {
		this.keep = keep;
	}

}