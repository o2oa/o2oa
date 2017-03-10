package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class DeniedException extends PromptException {

	private static final long serialVersionUID = 9107373129400635015L;

	DeniedException() {
		super("短信验证码不可为空.");
	}
}
