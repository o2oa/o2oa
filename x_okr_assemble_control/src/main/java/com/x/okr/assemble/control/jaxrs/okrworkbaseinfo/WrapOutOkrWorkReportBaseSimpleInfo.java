package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkReportBaseInfo;

@Wrap( OkrWorkReportBaseInfo.class)
public class WrapOutOkrWorkReportBaseSimpleInfo{

	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "汇报ID." )
	private String id;
	
	@EntityFieldDescribe( "工作汇报标题" )
	private String title = null;
	
	@EntityFieldDescribe( "工作汇报短标题" )
	private String shortTitle = null;
	
	@EntityFieldDescribe( "工作汇报当前环节" )
	private String activityName = "草稿";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
}
