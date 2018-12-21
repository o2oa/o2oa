package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.project.exception.PromptException;

class PageNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PageNotExistedException(String id) {
		super("page: {} not existed.", id);
	}
}
