package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	@FieldDescribe("重置身份")
	private List<String> distinguishedNameList;

	public List<String> getDistinguishedNameList() {
		return (null == distinguishedNameList) ? new ArrayList<>() : this.distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

}