package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBackTargetNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 3674258561301135294L;

	ExceptionGoBackTargetNotExist(String activity) {
		super("回退目标活动不存在, activity: {}.", activity);
	}

}