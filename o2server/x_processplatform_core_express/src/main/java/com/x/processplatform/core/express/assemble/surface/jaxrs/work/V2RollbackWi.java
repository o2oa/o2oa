package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2RollbackWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8300460200231747004L;

	@FieldDescribe("工作日志")
	private String workLog;

	@FieldDescribe("组织对象专有标识.")
	private List<String> distinguishedNameList;

	@FieldDescribe("办理意见.")
	private String opinion;

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

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