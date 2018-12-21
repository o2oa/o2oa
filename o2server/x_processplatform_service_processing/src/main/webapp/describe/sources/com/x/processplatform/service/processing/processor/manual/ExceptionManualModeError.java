package com.x.processplatform.service.processing.processor.manual;

import com.x.base.core.project.exception.RunningException;

class ExceptionManualModeError extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionManualModeError(String activityId) {
		super("unknown manualMode, id:{}.", activityId);
	}

}
