package com.x.processplatform.service.processing.processor;

import com.x.base.core.project.exception.RunningException;

class ExceptionProcessNotExist extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionProcessNotExist(String id) {
		super("process:{} not exist.", id);
	}

}
