package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvokeNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionInvokeNotExist(String flag) {
		super("调用接口: {} 不存在.", flag);
	}
}
