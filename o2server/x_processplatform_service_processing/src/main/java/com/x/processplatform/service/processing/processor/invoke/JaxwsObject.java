package com.x.processplatform.service.processing.processor.invoke;

import com.x.base.core.project.gson.GsonPropertyObject;

public class JaxwsObject extends GsonPropertyObject {

	private static final long serialVersionUID = -5078175531239493830L;

	private String method;

	private String address;

	private Object[] parameters;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

}
