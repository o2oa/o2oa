package com.x.portal.assemble.surface.jaxrs.menu;

import com.x.base.core.exception.PromptException;

class MenuNotExistedException extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	MenuNotExistedException(String id) {
		super("指定的目录不存在:{}.", id);
	}

}
