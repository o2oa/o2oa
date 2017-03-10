package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class InvalidMobileException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	InvalidMobileException(String mobile) {
		super("手机号格式错误:" + mobile + ".");
	}
}
