package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrStatisticReportContentCenter.class)
public class WrapOutOkrStatisticReportContentCenter{

	private String id = null;
	
	private String title = null;
	
	private Integer count = 0;
	
	private List<WrapOutOkrStatisticReportContent> contents = null;

	public String getTitle() {
		return title;
	}

	public Integer getCount() {
		return count;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<WrapOutOkrStatisticReportContent> getContents() {
		return contents;
	}

	public void setContents(List<WrapOutOkrStatisticReportContent> contents) {
		this.contents = contents;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer countAdd( Integer number ){
		this.count = this.count + number;
		return this.count;
	}
	
}
