package com.x.portal.assemble.designer.jaxrs.source;

import com.x.base.core.exception.PromptException;

class SourceNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	SourceNotExistedException(String id) {
		super("source: {} not existed.", id);
	}
}
