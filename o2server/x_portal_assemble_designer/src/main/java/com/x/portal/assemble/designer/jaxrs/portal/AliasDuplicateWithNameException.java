package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.exception.LanguagePromptException;

class AliasDuplicateWithNameException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	AliasDuplicateWithNameException(String name) {
		super("别名与名称重复: {}.", name);
	}
}
