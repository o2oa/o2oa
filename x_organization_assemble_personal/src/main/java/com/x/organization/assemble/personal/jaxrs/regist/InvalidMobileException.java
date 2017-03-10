package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.exception.PromptException;

class InvalidMobileException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InvalidMobileException(String mobile) {
		super("手机号格式错误:" + Objects.toString(mobile) + ".");
	}
}
