package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUpdateTask extends LanguagePromptException {

	private static final long serialVersionUID = 3674258561301135294L;

	ExceptionUpdateTask(String task) {
		super("更新待办失败, task: {}.", task);
	}

}