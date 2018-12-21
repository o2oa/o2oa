package com.x.processplatform.assemble.designer.jaxrs.process.demo;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInDemoSplit extends GsonPropertyObject {

	private String application;

	private String name;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
