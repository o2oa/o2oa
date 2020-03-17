package com.x.processplatform.assemble.designer.jaxrs.mapping;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataError extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDataError() {
		super("数据格式错误.");
	}

}
