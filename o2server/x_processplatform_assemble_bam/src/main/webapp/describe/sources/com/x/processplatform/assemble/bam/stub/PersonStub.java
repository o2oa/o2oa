package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.project.gson.GsonPropertyObject;

public class PersonStub extends GsonPropertyObject {

	private String name;
	
	private String value;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
