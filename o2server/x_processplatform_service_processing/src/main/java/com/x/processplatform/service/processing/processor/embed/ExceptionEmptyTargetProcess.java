package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.exception.RunningException;

class ExceptionEmptyTargetProcess extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionEmptyTargetProcess(String name) {
		super("embed:{} target process is empty.", name);
	}

}
