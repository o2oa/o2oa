package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.project.exception.PromptException;

class AliasDuplicateException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	AliasDuplicateException(String alias) {
		super("别名重复: {} .", alias);
	}
}
