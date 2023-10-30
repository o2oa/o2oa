package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2AddSplitWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5556104841725659921L;

	@FieldDescribe("添加的拆分值.")
	@Schema(description = "添加的拆分值.")
	private List<String> splitValueList;

	@FieldDescribe("是否排除已经存在的拆分值.")
	@Schema(description = "是否排除已经存在的拆分值.")
	private Boolean trimExist;

	@FieldDescribe("添加分支的工作日志标识.")
	@Schema(description = "添加分支的工作日志标识.")
	private String workLog;

	@FieldDescribe("路由.")
	private String routeName;
	@FieldDescribe("办理意见.")
	private String opinion;

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public List<String> getSplitValueList() {
		return splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

	public Boolean getTrimExist() {
		return trimExist;
	}

	public void setTrimExist(Boolean trimExist) {
		this.trimExist = trimExist;
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

}