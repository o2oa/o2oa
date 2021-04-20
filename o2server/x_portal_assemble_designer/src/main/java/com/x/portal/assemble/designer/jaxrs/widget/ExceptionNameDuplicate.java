package com.x.portal.assemble.designer.jaxrs.widget;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNameDuplicate(String name) {
		super("名称重复: {}.", name);
	}
}
