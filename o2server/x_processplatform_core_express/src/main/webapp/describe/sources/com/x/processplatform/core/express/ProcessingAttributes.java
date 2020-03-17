package com.x.processplatform.core.express;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ProcessingAttributes extends GsonPropertyObject {

	private Boolean debugger = false;

	private List<String> manualForceTaskIdentityList = new ArrayList<>();

//	private JsonElement routeData;
//
//	private String type;

	public Boolean getDebugger() {
		return debugger;
	}

	public void setDebugger(Boolean debugger) {
		this.debugger = debugger;
	}

	public List<String> getManualForceTaskIdentityList() {
		if (null == this.manualForceTaskIdentityList) {
			this.manualForceTaskIdentityList = new ArrayList<>();
		}
		return this.manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

//	public JsonElement getRouteData() {
//		return routeData;
//	}
//
//	public void setRouteData(JsonElement routeData) {
//		this.routeData = routeData;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}

}
