package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRetractNoneTaskCompleted extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRetractNoneTaskCompleted(String title, String id) {
		super("工作已经过处理无法召回, 工作:{}, id:{}.", title, id);
	}
}
