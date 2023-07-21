package com.x.query.assemble.designer.jaxrs.query;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAliasExist extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAliasExist(String str) {
		super("别名已存在:{}.", str);
	}

}
