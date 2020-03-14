package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;

public class TaskProperties extends JsonProperties {

	private List<String> prevTaskIdentityList;

//	private List<String> routeList;
//	private List<String> routeNameList;
//	private List<String> routeOpinionList;
//	private List<String> routeDecisionOpinionList;

//	public List<String> getRouteDecisionOpinionList() {
//		if (null == routeDecisionOpinionList) {
//			routeDecisionOpinionList = new ArrayList<String>();
//		}
//		return routeDecisionOpinionList;
//	}
//
//	public List<String> getRouteOpinionList() {
//		if (null == routeOpinionList) {
//			routeOpinionList = new ArrayList<String>();
//		}
//		return routeOpinionList;
//	}
//
//	public List<String> getRouteNameList() {
//		if (null == routeNameList) {
//			routeNameList = new ArrayList<String>();
//		}
//		return routeNameList;
//	}
//
//	public List<String> getRouteList() {
//		if (null == routeList) {
//			routeList = new ArrayList<String>();
//		}
//		return routeList;
//	}

	public List<String> getPrevTaskIdentityList() {
		if (null == prevTaskIdentityList) {
			this.prevTaskIdentityList = new ArrayList<String>();
		}
		return prevTaskIdentityList;
	}

	public void setPrevTaskIdentityList(List<String> prevTaskIdentityList) {
		this.prevTaskIdentityList = prevTaskIdentityList;
	}

//	public void setRouteList(List<String> routeList) {
//		this.routeList = routeList;
//	}
//
//	public void setRouteNameList(List<String> routeNameList) {
//		this.routeNameList = routeNameList;
//	}
//
//	public void setRouteOpinionList(List<String> routeOpinionList) {
//		this.routeOpinionList = routeOpinionList;
//	}
//
//	public void setRouteDecisionOpinionList(List<String> routeDecisionOpinionList) {
//		this.routeDecisionOpinionList = routeDecisionOpinionList;
//	}

}