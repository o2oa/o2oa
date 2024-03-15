package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionBeforeV82TaskUnsupportedReset extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionBeforeV82TaskUnsupportedReset(String id) {
		super("当前待办不支持重置处理人操作,请联系管理员.: {}.", id);
	}

}
