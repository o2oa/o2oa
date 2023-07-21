package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyQuery extends LanguagePromptException {

	private static final long serialVersionUID = 8891390969539772549L;

	ExceptionEmptyQuery() {
		super("搜索关键字不能为空.");
	}
}
