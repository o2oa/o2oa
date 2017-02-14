package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class)
public class WrapInOkrWorkBaseInfo extends OkrWorkBaseInfo {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	private List<String> workIds = null;
	
	//工作详细信息数据
	private String workDetail = null;//事项分解
	private String dutyDescription = null;
	private String landmarkDescription = null;
	private String majorIssuesDescription = null;
	private String progressAction = null; 
	private String progressPlan = null;
	private String resultDescription = null;
	
	private String checkSuccess = "success";
	private String description = null;

	public List<String> getWorkIds() {
		return workIds;
	}

	public void setWorkIds(List<String> workIds) {
		this.workIds = workIds;
	}

	public String getWorkDetail() {
		return workDetail;
	}

	public void setWorkDetail(String workDetail) {
		this.workDetail = workDetail;
	}

	public String getDutyDescription() {
		return dutyDescription;
	}

	public void setDutyDescription(String dutyDescription) {
		this.dutyDescription = dutyDescription;
	}

	public String getLandmarkDescription() {
		return landmarkDescription;
	}

	public void setLandmarkDescription(String landmarkDescription) {
		this.landmarkDescription = landmarkDescription;
	}

	public String getMajorIssuesDescription() {
		return majorIssuesDescription;
	}

	public void setMajorIssuesDescription(String majorIssuesDescription) {
		this.majorIssuesDescription = majorIssuesDescription;
	}

	public String getProgressAction() {
		return progressAction;
	}

	public void setProgressAction(String progressAction) {
		this.progressAction = progressAction;
	}

	public String getProgressPlan() {
		return progressPlan;
	}

	public void setProgressPlan(String progressPlan) {
		this.progressPlan = progressPlan;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public String getCheckSuccess() {
		return checkSuccess;
	}

	public void setCheckSuccess(String checkSuccess) {
		this.checkSuccess = checkSuccess;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
}