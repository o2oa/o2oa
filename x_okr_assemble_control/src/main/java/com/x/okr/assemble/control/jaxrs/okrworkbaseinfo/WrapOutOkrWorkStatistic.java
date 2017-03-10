package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class )
public class WrapOutOkrWorkStatistic  {
	
	public static List<String> Excludes = new ArrayList<String>();
	
	private String name = null;
	private Long responWorkTotal = 0L;
	private Long responProcessingWorkCount = 0L;
	private Long responCompletedWorkCount = 0L;
	private Long draftWorkCount = 0L;
	private Long overtimeResponWorkCount = 0L;
	private Long overtimeCooperWorkCount = 0L;
	private Long overtimeDeployWorkCount = 0L;
	private Long overtimenessResponWorkCount = 0L;
	private Long overtimenessCooperWorkCount = 0L;
	private Long overtimenessDeployWorkCount = 0L;
	private Double percent = 0.0;
	public String getName() {
		return name;
	}
	public Long getResponWorkTotal() {
		return responWorkTotal;
	}
	public Long getResponProcessingWorkCount() {
		return responProcessingWorkCount;
	}
	public Long getResponCompletedWorkCount() {
		return responCompletedWorkCount;
	}
	public Long getDraftWorkCount() {
		return draftWorkCount;
	}
	public Long getOvertimeResponWorkCount() {
		return overtimeResponWorkCount;
	}
	public Long getOvertimeCooperWorkCount() {
		return overtimeCooperWorkCount;
	}
	public Long getOvertimeDeployWorkCount() {
		return overtimeDeployWorkCount;
	}
	public Long getOvertimenessResponWorkCount() {
		return overtimenessResponWorkCount;
	}
	public Long getOvertimenessCooperWorkCount() {
		return overtimenessCooperWorkCount;
	}
	public Long getOvertimenessDeployWorkCount() {
		return overtimenessDeployWorkCount;
	}
	public Double getPercent() {
		return percent;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setResponWorkTotal(Long responWorkTotal) {
		this.responWorkTotal = responWorkTotal;
	}
	public void setResponProcessingWorkCount(Long responProcessingWorkCount) {
		this.responProcessingWorkCount = responProcessingWorkCount;
	}
	public void setResponCompletedWorkCount(Long responCompletedWorkCount) {
		this.responCompletedWorkCount = responCompletedWorkCount;
	}
	public void setDraftWorkCount(Long draftWorkCount) {
		this.draftWorkCount = draftWorkCount;
	}
	public void setOvertimeResponWorkCount(Long overtimeResponWorkCount) {
		this.overtimeResponWorkCount = overtimeResponWorkCount;
	}
	public void setOvertimeCooperWorkCount(Long overtimeCooperWorkCount) {
		this.overtimeCooperWorkCount = overtimeCooperWorkCount;
	}
	public void setOvertimeDeployWorkCount(Long overtimeDeployWorkCount) {
		this.overtimeDeployWorkCount = overtimeDeployWorkCount;
	}
	public void setOvertimenessResponWorkCount(Long overtimenessResponWorkCount) {
		this.overtimenessResponWorkCount = overtimenessResponWorkCount;
	}
	public void setOvertimenessCooperWorkCount(Long overtimenessCooperWorkCount) {
		this.overtimenessCooperWorkCount = overtimenessCooperWorkCount;
	}
	public void setOvertimenessDeployWorkCount(Long overtimenessDeployWorkCount) {
		this.overtimenessDeployWorkCount = overtimenessDeployWorkCount;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	
}