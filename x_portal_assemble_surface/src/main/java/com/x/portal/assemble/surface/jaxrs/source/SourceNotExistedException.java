package com.x.portal.assemble.surface.jaxrs.source;

import com.x.base.core.exception.PromptException;

class SourceNotExistedException extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	SourceNotExistedException(String id) {
		super("指定的数据源不存在:{}.", id);
	}

}
