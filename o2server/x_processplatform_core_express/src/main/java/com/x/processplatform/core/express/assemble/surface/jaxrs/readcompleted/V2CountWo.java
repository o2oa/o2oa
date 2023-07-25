package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2CountWo extends GsonPropertyObject {

	private static final long serialVersionUID = -8723288394987103327L;

	@FieldDescribe("总数量.")
	@Schema(description = "总数量.")
	private Long count;

	@FieldDescribe("按应用分类数量.")
	@Schema(description = "按应用分类数量.")
	private List<NameValueCountPair> applicationList = new ArrayList<>();

	@FieldDescribe("按流程分类数量.")
	@Schema(description = "按流程分类数量.")
	private List<NameValueCountPair> processList = new ArrayList<>();

	@FieldDescribe("按创建人分类数量.")
	@Schema(description = "按创建人分类数量.")
	private List<NameValueCountPair> creatorPersonList = new ArrayList<>();

	@FieldDescribe("按创建组织分类数量.")
	@Schema(description = "按创建组织分类数量.")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();

	@FieldDescribe("按创建的年月分类.")
	@Schema(description = "按创建的年月分类.")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<NameValueCountPair> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<NameValueCountPair> applicationList) {
		this.applicationList = applicationList;
	}

	public List<NameValueCountPair> getProcessList() {
		return processList;
	}

	public void setProcessList(List<NameValueCountPair> processList) {
		this.processList = processList;
	}

	public List<NameValueCountPair> getCreatorPersonList() {
		return creatorPersonList;
	}

	public void setCreatorPersonList(List<NameValueCountPair> creatorPersonList) {
		this.creatorPersonList = creatorPersonList;
	}

	public List<NameValueCountPair> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<NameValueCountPair> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

	public List<NameValueCountPair> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

}