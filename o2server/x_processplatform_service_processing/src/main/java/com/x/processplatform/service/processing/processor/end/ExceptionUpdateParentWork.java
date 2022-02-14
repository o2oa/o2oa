package com.x.processplatform.service.processing.processor.end;

import com.x.base.core.project.exception.RunningException;

class ExceptionUpdateParentWork extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionUpdateParentWork(String work, String parentWork) {
		super("updateParentWork error, work: {}, parent work:{}.", work, parentWork);
	}

}
