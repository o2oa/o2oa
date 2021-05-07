package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyTrainingSet extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyTrainingSet(String title, String id, String person) {
		super("待办:{},id:{},处理人:{},数据集为空.", title, id, person);
	}
}
