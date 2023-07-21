package com.x.processplatform.assemble.surface.jaxrs.service;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionService extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionService(String workId) {
		super("服务执行失败,工作:{}.", workId);
	}
}
