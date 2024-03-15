package com.x.processplatform.core.express.service.processing.jaxrs.workcompleted;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionRollbackWi extends GsonPropertyObject {

	private static final long serialVersionUID = 2040132891703254119L;

	@FieldDescribe("工作日志.")
	private String workLog;

	@FieldDescribe("回溯组织专用标识.")
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
