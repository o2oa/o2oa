package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.exception.PromptException;

class TemplatePageNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	TemplatePageNotExistedException(String id) {
		super("page: {} not existed.", id);
	}
}
