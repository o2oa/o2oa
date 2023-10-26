package com.x.processplatform.core.express.service.processing.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2UpdatePrevTaskWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4349122546797296345L;

	@FieldDescribe("任务标识.")
	private String job;
	@FieldDescribe("上一环节串号.")
	private String prevSeries;
	@FieldDescribe("当前环节串号.")
	private String series;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getPrevSeries() {
		return prevSeries;
	}

	public void setPrevSeries(String prevSeries) {
		this.prevSeries = prevSeries;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

}