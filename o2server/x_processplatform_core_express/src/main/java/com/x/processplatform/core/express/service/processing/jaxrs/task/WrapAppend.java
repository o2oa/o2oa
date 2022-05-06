package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapAppend extends GsonPropertyObject {

	private static final long serialVersionUID = -8843505395291348770L;
	
	@FieldDescribe("添加的待办身份.")
	private List<String> identityList;

	public List<String> getIdentityList() {
		if (null == identityList) {
			this.identityList = new ArrayList<String>();
		}
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

}
