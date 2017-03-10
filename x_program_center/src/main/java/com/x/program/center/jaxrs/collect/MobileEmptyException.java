package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class MobileEmptyException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	MobileEmptyException() {
		super("手机号不能为空.");
	}
}
