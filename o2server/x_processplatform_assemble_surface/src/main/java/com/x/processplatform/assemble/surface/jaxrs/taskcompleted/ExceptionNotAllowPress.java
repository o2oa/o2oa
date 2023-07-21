package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotAllowPress extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNotAllowPress(String activityName) {
		super("{} 节点不允许发起提醒.", activityName);
	}
}
