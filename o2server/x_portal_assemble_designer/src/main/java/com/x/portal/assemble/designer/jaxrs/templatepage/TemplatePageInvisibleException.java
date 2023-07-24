package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.LanguagePromptException;

class TemplatePageInvisibleException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	TemplatePageInvisibleException(String person, String name, String id) {
		super("用户: {} 无权限访问此模板页面: {} id: {}.", person, name, id);
	}
}
