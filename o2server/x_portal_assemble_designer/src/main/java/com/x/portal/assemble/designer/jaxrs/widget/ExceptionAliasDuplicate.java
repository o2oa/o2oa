package com.x.portal.assemble.designer.jaxrs.widget;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAliasDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAliasDuplicate(String alias) {
		super("别名重复: {}.", alias);
	}
}
