package com.x.processplatform.service.processing.processor.invoke;

import java.util.Map;

import com.x.base.core.project.gson.GsonPropertyObject;

public class JaxrsObject extends GsonPropertyObject {

	private static final long serialVersionUID = -1094802895782587629L;

	private String method;

	private String body;

	private String address;

	private String contentType;

	private Map<String, String> head;

	public Map<String, String> getHead() {
		return head;
	}

	public void setHead(Map<String, String> head) {
		this.head = head;
	}

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
