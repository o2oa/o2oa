package com.x.base.core.project.connection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HttpConnection对象
 * 
 * @author ray
 *
 */

public class HttpConnectionResponse {

	private Integer responseCode;

	private String body;

	private Map<String, String> headers = new LinkedHashMap<>();

	private byte[] bytes;

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
