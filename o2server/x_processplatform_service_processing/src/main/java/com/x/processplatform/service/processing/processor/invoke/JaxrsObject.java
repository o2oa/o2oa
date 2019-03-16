package com.x.processplatform.service.processing.processor.invoke;

import com.x.base.core.project.gson.GsonPropertyObject;

public class JaxrsObject extends GsonPropertyObject {

	private String method;

	private String body;

	private String address;

	private String contentType;

	private Boolean internal;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

}
