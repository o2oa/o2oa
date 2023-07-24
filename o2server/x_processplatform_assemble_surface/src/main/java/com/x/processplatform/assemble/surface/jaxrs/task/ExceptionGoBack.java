package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBack extends LanguagePromptException {

	private static final long serialVersionUID = 3674258561301135294L;

	ExceptionGoBack(String activity, String task) {
		super("回退失败, activity: {}, task:{}.", activity, task);
	}

}