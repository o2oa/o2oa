package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPageNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionPageNotExist(String id) {
		super("指定的页面不存在:{}.", id);
	}

}
