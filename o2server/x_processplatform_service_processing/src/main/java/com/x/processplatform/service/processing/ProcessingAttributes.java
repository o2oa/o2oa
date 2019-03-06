package com.x.processplatform.service.processing;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ProcessingAttributes extends GsonPropertyObject {

	private Boolean debugger = false;

	private JsonElement routeData;

	public Boolean getDebugger() {
		return debugger;
	}

	public void setDebugger(Boolean debugger) {
		this.debugger = debugger;
	}

	public JsonElement getRouteData() {
		return routeData;
	}

	public void setRouteData(JsonElement routeData) {
		this.routeData = routeData;
	}

}
