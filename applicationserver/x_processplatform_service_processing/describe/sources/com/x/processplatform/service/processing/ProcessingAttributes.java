package com.x.processplatform.service.processing;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ProcessingAttributes extends GsonPropertyObject {

	private Boolean debugger = false;

	public Boolean getDebugger() {
		return debugger;
	}

	public void setDebugger(Boolean debugger) {
		this.debugger = debugger;
	}

}
