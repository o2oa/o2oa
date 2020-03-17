package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.exception.PromptException;

class NameDuplicateException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	NameDuplicateException(String name) {
		super("名称重复: {} .", name);
	}
}
