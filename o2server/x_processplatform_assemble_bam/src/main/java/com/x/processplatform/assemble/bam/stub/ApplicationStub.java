package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ApplicationStub extends GsonPropertyObject {

	private String name;
	private String value;
	private String category;

	private ProcessStubs processStubs = new ProcessStubs();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProcessStubs getProcessStubs() {
		return processStubs;
	}

	public void setProcessStubs(ProcessStubs processStubs) {
		this.processStubs = processStubs;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
