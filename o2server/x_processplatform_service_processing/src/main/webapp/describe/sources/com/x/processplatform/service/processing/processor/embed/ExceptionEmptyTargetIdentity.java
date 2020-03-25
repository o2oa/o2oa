package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.exception.RunningException;

class ExceptionEmptyTargetIdentity extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionEmptyTargetIdentity(String name) {
		super("embed:{} target identity is empty.", name);
	}

}
