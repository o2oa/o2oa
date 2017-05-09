package com.x.portal.assemble.designer.jaxrs.menu;

import com.x.base.core.exception.PromptException;

class MenuNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	MenuNotExistedException(String id) {
		super("menu: {} not existed.", id);
	}
}
