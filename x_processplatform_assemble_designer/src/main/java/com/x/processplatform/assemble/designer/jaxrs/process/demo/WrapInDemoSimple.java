package com.x.processplatform.assemble.designer.jaxrs.process.demo;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Process;

@Wrap(Process.class)
public class WrapInDemoSimple extends GsonPropertyObject {

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
