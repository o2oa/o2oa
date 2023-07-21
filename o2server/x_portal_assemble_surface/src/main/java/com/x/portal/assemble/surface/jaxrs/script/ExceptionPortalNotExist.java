package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPortalNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionPortalNotExist(String id) {
		super("指定的站点不存在:{}.", id);
	}

}
