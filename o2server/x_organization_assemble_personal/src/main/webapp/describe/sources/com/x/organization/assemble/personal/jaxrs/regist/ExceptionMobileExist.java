package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionMobileExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionMobileExist(String mobile) {
		super("手机:" + Objects.toString(mobile) + "已注册.");
	}
}
