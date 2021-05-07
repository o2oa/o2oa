package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class NameDuplicateException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	NameDuplicateException(String name) {
		super("名称重复: {}.", name);
	}
}
