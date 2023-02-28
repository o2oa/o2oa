package com.x.portal.assemble.designer.jaxrs.dict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -778285754125984911L;

	ExceptionApplicationNotExist(String flag) {
		super("指定的应用不存在:{}.", flag);
	}
}
