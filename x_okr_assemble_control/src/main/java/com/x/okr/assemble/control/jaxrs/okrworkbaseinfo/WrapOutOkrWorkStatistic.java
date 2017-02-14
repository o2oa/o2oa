package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class )
public class WrapOutOkrWorkStatistic  {
	
	public static List<String> Excludes = new ArrayList<String>();
	
	private String name = null;
	private Long workTotal = 0L;
	private Long processingWorkCount = 0L;
	private Long completedWorkCount = 0L;
	private Long overtimeWorkCount = 0L;
	private Long draftWorkCount = 0L;
	private Double percent = 0.0;
	
	public Long getWorkTotal() {
		return workTotal;
	}
	public void setWorkTotal(Long workTotal) {
		this.workTotal = workTotal;
	}
	public Long getProcessingWorkCount() {
		return processingWorkCount;
	}
	public void setProcessingWorkCount(Long processingWorkCount) {
		this.processingWorkCount = processingWorkCount;
	}
	public Long getCompletedWorkCount() {
		return completedWorkCount;
	}
	public void setCompletedWorkCount(Long completedWorkCount) {
		this.completedWorkCount = completedWorkCount;
	}
	public Long getOvertimeWorkCount() {
		return overtimeWorkCount;
	}
	public void setOvertimeWorkCount(Long overtimeWorkCount) {
		this.overtimeWorkCount = overtimeWorkCount;
	}
	public Long getDraftWorkCount() {
		return draftWorkCount;
	}
	public void setDraftWorkCount(Long draftWorkCount) {
		this.draftWorkCount = draftWorkCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	
}