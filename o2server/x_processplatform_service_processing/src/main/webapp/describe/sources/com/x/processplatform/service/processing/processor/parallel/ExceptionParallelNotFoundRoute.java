package com.x.processplatform.service.processing.processor.parallel;

import com.x.base.core.project.exception.RunningException;

class ExceptionParallelNotFoundRoute extends RunningException {

	private static final long serialVersionUID = 6143035361950594561L;

	ExceptionParallelNotFoundRoute(String name, String title, String id, String job) {
		super("parallel:{} not found route, work:{}, id:{}, job:{}.", name, title, id, job);
	}

}
