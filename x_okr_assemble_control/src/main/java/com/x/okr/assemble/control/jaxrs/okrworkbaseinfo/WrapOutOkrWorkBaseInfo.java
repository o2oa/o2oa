package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapOutOkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class )
public class WrapOutOkrWorkBaseInfo extends OkrWorkBaseInfo  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	private List< WrapOutOkrWorkBaseInfo > subWrapOutOkrWorkBaseInfos = null;
	private List< WrapOutOkrWorkAuthorizeRecord > okrWorkAuthorizeRecords = null;
	private WrapOutOkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
	private String workOutType = "SUBWORK";
	private String workDetail = null;
	private String dutyDescription = null;
	private String landmarkDescription = null;
	private String majorIssuesDescription = null;
	private String progressAction = null;
	private String progressPlan = null;
	private String resultDescription = null;
    private Boolean hasNoneSubmitReport = false;
	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	public List<WrapOutOkrWorkBaseInfo> getSubWrapOutOkrWorkBaseInfos() {
		return subWrapOutOkrWorkBaseInfos;
	}

	public void setSubWrapOutOkrWorkBaseInfos(List<WrapOutOkrWorkBaseInfo> subWrapOutOkrWorkBaseInfos) {
		this.subWrapOutOkrWorkBaseInfos = subWrapOutOkrWorkBaseInfos;
	}

	public void addNewSubWorkBaseInfo(WrapOutOkrWorkBaseInfo workBaseInfo) {
		if( this.subWrapOutOkrWorkBaseInfos == null ){
			this.subWrapOutOkrWorkBaseInfos = new ArrayList<WrapOutOkrWorkBaseInfo>();
		}
		if( !subWrapOutOkrWorkBaseInfos.contains( workBaseInfo )){
			subWrapOutOkrWorkBaseInfos.add( workBaseInfo );
		}
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

	/**
	 * 判断是父级工作还是子工作
	 * @return
	 */
	public String getWorkOutType() {
		return workOutType;
	}

	/**
	 * 判断是父级工作还是子工作
	 * @param workOutType
	 */
	public void setWorkOutType(String workOutType) {
		this.workOutType = workOutType;
	}

	public List<WrapOutOkrWorkAuthorizeRecord> getOkrWorkAuthorizeRecords() {
		return okrWorkAuthorizeRecords;
	}

	public void setOkrWorkAuthorizeRecords(List<WrapOutOkrWorkAuthorizeRecord> okrWorkAuthorizeRecords) {
		this.okrWorkAuthorizeRecords = okrWorkAuthorizeRecords;
	}

	public WrapOutOkrWorkAuthorizeRecord getOkrWorkAuthorizeRecord() {
		return okrWorkAuthorizeRecord;
	}

	public void setOkrWorkAuthorizeRecord(WrapOutOkrWorkAuthorizeRecord okrWorkAuthorizeRecord) {
		this.okrWorkAuthorizeRecord = okrWorkAuthorizeRecord;
	}

	public Boolean getHasNoneSubmitReport() {
		return hasNoneSubmitReport;
	}

	public void setHasNoneSubmitReport(Boolean hasNoneSubmitReport) {
		this.hasNoneSubmitReport = hasNoneSubmitReport;
	}
	
}