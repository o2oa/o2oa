package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import java.util.List;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceSetting.class)
public class WrapInFilter extends GsonPropertyObject {

	private List<String> appIdList;

	private String key;	

	public List<String> getAppIdList() {
		return appIdList;
	}

	public void setAppIdList(List<String> appIdList) {
		this.appIdList = appIdList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
