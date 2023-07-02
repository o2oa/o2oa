package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessingTask extends LanguagePromptException {

	private static final long serialVersionUID = 3674258561301135294L;

	ExceptionProcessingTask(String work) {
		super("记录处理失败, work: {}.", work);
	}

}
