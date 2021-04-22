package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPortalAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionPortalAccessDenied(String person, String name, String id) {
		super("用户: {}, 访问站点: {}, id: {}, 失败,权限不足.", person, name, id);
	}

}
