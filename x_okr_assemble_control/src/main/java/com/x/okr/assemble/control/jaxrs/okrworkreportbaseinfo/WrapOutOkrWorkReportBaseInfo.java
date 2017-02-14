package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.WrapOutOkrWorkReportProcessLog;
import com.x.okr.entity.OkrWorkReportBaseInfo;
@Wrap( OkrWorkReportBaseInfo.class)
public class WrapOutOkrWorkReportBaseInfo extends OkrWorkReportBaseInfo{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
	
	private Boolean isReporter = false;
	
	private Boolean isWorkAdmin = false;
	
	private Boolean isReadLeader = false;
	
	private Boolean isCreator = false;
	
	/**
	 * 管理员督办信息
	 */
	private String adminSuperviseInfo = "";
	
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
	 * 说明备注信息
	 */
	private String memo = "";
	
	private List<WrapOutOkrWorkReportProcessLog> processLogs = null;

	private WrapOutOkrWorkBaseInfo workInfo = null;
	
	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	public Boolean getIsReporter() {
		return isReporter;
	}

	public void setIsReporter(Boolean isReporter) {
		this.isReporter = isReporter;
	}

	public Boolean getIsWorkAdmin() {
		return isWorkAdmin;
	}

	public void setIsWorkAdmin(Boolean isWorkAdmin) {
		this.isWorkAdmin = isWorkAdmin;
	}

	public Boolean getIsReadLeader() {
		return isReadLeader;
	}

	public void setIsReadLeader(Boolean isReadLeader) {
		this.isReadLeader = isReadLeader;
	}

	public Boolean getIsCreator() {
		return isCreator;
	}

	public void setIsCreator(Boolean isCreator) {
		this.isCreator = isCreator;
	}
	
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

	public List<WrapOutOkrWorkReportProcessLog> getProcessLogs() {
		return processLogs;
	}

	public void setProcessLogs(List<WrapOutOkrWorkReportProcessLog> processLogs) {
		this.processLogs = processLogs;
	}

	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}

	public void setAdminSuperviseInfo(String adminSuperviseInfo) {
		this.adminSuperviseInfo = adminSuperviseInfo;
	}

	public WrapOutOkrWorkBaseInfo getWorkInfo() {
		return workInfo;
	}

	public void setWorkInfo(WrapOutOkrWorkBaseInfo workInfo) {
		this.workInfo = workInfo;
	}
	
}
