package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.exception.PromptException;

class ExceptionMoveDenied extends PromptException {

	private static final long serialVersionUID = -5921306337885212054L;

	ExceptionMoveDenied() {
		super("不能跨共享区转移.");
	}
}
