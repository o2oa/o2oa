package com.x.okr.assemble.control.jaxrs.statistic;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrReportSubmitStatusStatisticHeader.class)
public class WrapOutOkrReportSubmitStatusStatisticHeader{

	private String title = null;
	
	private String startDate = null;
	
	private String endDate = null;
	
	private String description = null;
	
	private Integer width = null;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
