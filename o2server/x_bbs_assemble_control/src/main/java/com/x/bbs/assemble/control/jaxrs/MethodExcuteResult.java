package com.x.bbs.assemble.control.jaxrs;

public class MethodExcuteResult {
	
	private Boolean success = true;
	private String message = "success";
	private Exception error = null;
	private Object backObject = null;
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Exception getError() {
		return error;
	}
	public void error(Exception e) {
		this.error = e;
	}
	public Object getBackObject() {
		return backObject;
	}
	public void setBackObject(Object backObject) {
		this.backObject = backObject;
	}
}
