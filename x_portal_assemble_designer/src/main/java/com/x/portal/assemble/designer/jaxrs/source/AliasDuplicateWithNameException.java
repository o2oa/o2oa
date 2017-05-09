package com.x.portal.assemble.designer.jaxrs.source;

import com.x.base.core.exception.PromptException;

class AliasDuplicateWithNameException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	AliasDuplicateWithNameException(String name) {
		super("别名与名称重复: {} .", name);
	}
}
