package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionDeleteTypeCmsWi extends GsonPropertyObject {

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	private static final long serialVersionUID = -2304428900858206878L;
	@FieldDescribe("关联内容标识.")
	private List<String> idList;

}