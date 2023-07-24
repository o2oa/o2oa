package com.x.portal.assemble.designer.jaxrs.widget;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAliasDuplicateWithName extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAliasDuplicateWithName(String name) {
		super("别名与名称重复: {}.", name);
	}
}
