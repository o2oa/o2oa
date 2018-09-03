package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionDenied extends PromptException {

	private static final long serialVersionUID = 9107373129400635015L;

	ExceptionDenied() {
		super("短信验证码不可为空.");
	}
}
