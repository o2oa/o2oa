package com.x.processplatform.assemble.surface.jaxrs.queryview;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryViewNotExist extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionQueryViewNotExist(String flag) {
		super("指定的应用不存在:{}.", flag);
	}

}
