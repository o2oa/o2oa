package com.x.organization.assemble.control.jaxrs.person;

import java.util.Objects;

import com.x.base.core.exception.PromptException;

 class InvalidMobileException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	 InvalidMobileException(String name) {
		super("手机号错误,不能为空,且必须是11为手机号码:" + Objects.toString(name) + ".");
	}
}
