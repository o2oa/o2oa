package com.x.query.assemble.designer.jaxrs.view;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataEmpty extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionDataEmpty() {
		super("访问计划为空.");
	}
}
