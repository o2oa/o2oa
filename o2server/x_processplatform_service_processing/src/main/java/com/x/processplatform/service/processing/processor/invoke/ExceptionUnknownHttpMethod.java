package com.x.processplatform.service.processing.processor.invoke;

import com.x.base.core.project.exception.RunningException;

class ExceptionUnknownHttpMethod extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionUnknownHttpMethod(String method) {
		super("未知http method:{}.",method);
	}

}
