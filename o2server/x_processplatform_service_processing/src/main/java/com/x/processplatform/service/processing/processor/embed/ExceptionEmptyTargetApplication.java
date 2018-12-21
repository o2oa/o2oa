package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.exception.RunningException;

class ExceptionEmptyTargetApplication extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionEmptyTargetApplication(String name) {
		super("embed:{} target application is empty.", name);
	}

}
