package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import java.util.List;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceSetting.class)
public class WrapInFilter extends GsonPropertyObject {

	private List<NameValueCountPair> appIdList;

	private String key;	

	public List<NameValueCountPair> getAppIdList() {
		return appIdList;
	}

	public void setAppIdList(List<NameValueCountPair> appIdList) {
		this.appIdList = appIdList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
