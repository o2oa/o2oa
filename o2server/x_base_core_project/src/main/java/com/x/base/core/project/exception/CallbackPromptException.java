package com.x.base.core.project.exception;

public abstract class CallbackPromptException extends PromptException {

	private static final long serialVersionUID = 5286855494725725798L;

	private String callbackName;

	public CallbackPromptException(String callbackName, String message, Object... os) {
		super(format(message, os));
		this.callbackName = callbackName;
	}

	public String getCallbackName() {
		return callbackName;
	}

	public void setCallbackName(String callbackName) {
		this.callbackName = callbackName;
	}

}