package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.exception.PromptException;

class PageNotExistedException extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	PageNotExistedException(String id) {
		super("指定的页面不存在:{}.", id);
	}

}
