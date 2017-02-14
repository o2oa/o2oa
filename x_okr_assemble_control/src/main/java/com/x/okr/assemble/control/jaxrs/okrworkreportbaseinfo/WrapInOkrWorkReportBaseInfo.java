package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkReportBaseInfo;

@Wrap( OkrWorkReportBaseInfo.class)
public class WrapInOkrWorkReportBaseInfo extends OkrWorkReportBaseInfo {

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	private String workPointAndRequirements = "";
	/**
	 * 填写汇报时填写的具体进展描述信息
	 */
	private String progressDescription = "";
	/**
	 * 下一步工作计划信息
	 */
	private String workPlan = "";
	/**
	 * 管理员督办信息
	 */
	private String adminSuperviseInfo = "";
	/**
	 * 说明备注信息
	 */
	private String memo = "";
	
	private String opinion = "";
	
	public String getProgressDescription() {
		return progressDescription;
	}
	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}
	
	public String getWorkPlan() {
		return workPlan;
	}
	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}
	
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getWorkPointAndRequirements() {
		return workPointAndRequirements;
	}
	public void setWorkPointAndRequirements(String workPointAndRequirements) {
		this.workPointAndRequirements = workPointAndRequirements;
	}
	
	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}
	public void setAdminSuperviseInfo(String adminSuperviseInfo) {
		this.adminSuperviseInfo = adminSuperviseInfo;
	}
	
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	
}
