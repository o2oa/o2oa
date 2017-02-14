package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrTask;

@Wrap( OkrTask.class)
public class WrapOutOkrTaskCollect extends OkrTask{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
	
	private String activity = null;
	
	private Integer count = 0;
	
	private WrapOutOkrTaskCollectList reportCollect = null;

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public WrapOutOkrTaskCollectList getReportCollect() {
		return reportCollect;
	}

	public void setReportCollect(WrapOutOkrTaskCollectList reportCollect) {
		this.reportCollect = reportCollect;
	}
	
}
