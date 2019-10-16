package com.x.base.core.project.jaxrs.fireschedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionScheduleLastNotEnd extends PromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionScheduleLastNotEnd(String className, String application) {
		super("abandon fire schedule className: {}, application: {}, last run not ended.", className, application);
	}
}