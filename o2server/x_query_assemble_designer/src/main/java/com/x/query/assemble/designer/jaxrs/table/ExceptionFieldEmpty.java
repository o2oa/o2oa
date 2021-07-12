package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFieldEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFieldEmpty() {
		super("空内容.");
	}
}
