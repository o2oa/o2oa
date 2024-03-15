package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2RollbackWi extends GsonPropertyObject {

	private static final long serialVersionUID = 4148950553127792266L;

	@FieldDescribe("工作日志")
	private String workLog;

	@FieldDescribe("组织对象专有标识.")
	private List<String> distinguishedNameList;

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

	public List<String> getDistinguishedNameList() {
		return distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

}