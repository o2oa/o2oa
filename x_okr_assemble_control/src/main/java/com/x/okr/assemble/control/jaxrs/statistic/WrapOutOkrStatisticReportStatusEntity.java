package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.timertask.entity.WorkBaseReportSubmitEntity;

@Wrap( WrapOutOkrStatisticReportStatusEntity.class)
public class WrapOutOkrStatisticReportStatusEntity{

	private String id = null;
	
	private String title = null;
	
	private String deployDate = null;
	
	private String completeLimitDate = null;
	
	private List<WorkBaseReportSubmitEntity> fields = null;
	
	private List<WrapOutOkrStatisticReportStatusEntity> array = null;
	
	private Integer rowCount = 0;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<WrapOutOkrStatisticReportStatusEntity> getArray() {
		return array;
	}

	public void setArray(List<WrapOutOkrStatisticReportStatusEntity> array) {
		this.array = array;
	}

	public Integer getRowCount() {
		return rowCount;
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}	
	
	public void addRowCount( Integer number ){
		this.rowCount = this.rowCount + number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeployDate() {
		return deployDate;
	}

	public void setDeployDate(String deployDate) {
		this.deployDate = deployDate;
	}

	public String getCompleteLimitDate() {
		return completeLimitDate;
	}

	public void setCompleteLimitDate(String completeLimitDate) {
		this.completeLimitDate = completeLimitDate;
	}

	public List<WorkBaseReportSubmitEntity> getFields() {
		return fields;
	}

	public void setFields(List<WorkBaseReportSubmitEntity> fields) {
		this.fields = fields;
	}
	
	
}
