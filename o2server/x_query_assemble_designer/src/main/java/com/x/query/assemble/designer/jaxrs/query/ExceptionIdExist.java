package com.x.query.assemble.designer.jaxrs.query;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdExist extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionIdExist(String str) {
		super("标识已存在:{}.", str);
	}

}
