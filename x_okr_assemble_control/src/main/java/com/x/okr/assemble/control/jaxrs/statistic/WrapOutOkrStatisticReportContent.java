package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.timertask.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrStatisticReportContent;

@Wrap( WrapOutOkrStatisticReportContent.class)
public class WrapOutOkrStatisticReportContent extends OkrStatisticReportContent{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
	
	private String serialNumber = "1";
	
	private Integer level = 1;
	
	private String workDetail = null;
	
	private String progressAction = null;
	
	private String landmarkDescription = null;
	
	private Boolean hasSubWork = false;
    
	private List<WorkReportProcessOpinionEntity> opinions = null;
	
	private List<WrapOutOkrStatisticReportContent> subWork = null;

	public List<WorkReportProcessOpinionEntity> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<WorkReportProcessOpinionEntity> opinions) {
		this.opinions = opinions;
	}

	public String getWorkDetail() {
		return workDetail;
	}

	public void setWorkDetail(String workDetail) {
		this.workDetail = workDetail;
	}

	public String getProgressAction() {
		return progressAction;
	}

	public void setProgressAction(String progressAction) {
		this.progressAction = progressAction;
	}

	public String getLandmarkDescription() {
		return landmarkDescription;
	}

	public void setLandmarkDescription(String landmarkDescription) {
		this.landmarkDescription = landmarkDescription;
	}

	public Boolean getHasSubWork() {
		return hasSubWork;
	}

	public void setHasSubWork( Boolean hasSubWork) {
		this.hasSubWork = hasSubWork;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<WrapOutOkrStatisticReportContent> getSubWork() {
		return subWork;
	}

	public void setSubWork(List<WrapOutOkrStatisticReportContent> subWork) {
		this.subWork = subWork;
	}
	
	public void addSubWork( WrapOutOkrStatisticReportContent work ){
		if( this.subWork == null ){
			this.subWork = new ArrayList<>();
		}
		this.subWork.add( work );
	}
	
}
