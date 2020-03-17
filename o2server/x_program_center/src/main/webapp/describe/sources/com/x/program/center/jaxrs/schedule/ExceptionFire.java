package com.x.program.center.jaxrs.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionFire extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionFire(Throwable throwable, String className, String application, String node) {
		super(throwable, "error fire schedule: className: {}, applciation: {}, node: {}.", className, application,
				node);
	}
}
