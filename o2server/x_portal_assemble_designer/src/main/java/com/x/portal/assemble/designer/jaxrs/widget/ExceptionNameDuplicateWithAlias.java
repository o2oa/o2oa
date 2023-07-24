package com.x.portal.assemble.designer.jaxrs.widget;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameDuplicateWithAlias extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNameDuplicateWithAlias(String name) {
		super("页面名称与别名重复: {}.", name);
	}
}
