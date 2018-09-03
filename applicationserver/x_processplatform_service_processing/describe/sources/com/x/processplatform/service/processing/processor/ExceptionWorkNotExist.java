package com.x.processplatform.service.processing.processor;

import com.x.base.core.project.exception.RunningException;

class ExceptionWorkNotExist extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionWorkNotExist(String id) {
		super("work:{} not exist.", id);
	}

}
