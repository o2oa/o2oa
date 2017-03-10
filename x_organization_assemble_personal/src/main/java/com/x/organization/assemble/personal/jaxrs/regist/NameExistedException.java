package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.exception.PromptException;

class NameExistedException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NameExistedException(String name) {
		super("用户:" + Objects.toString(name) + "已注册.");
	}
}
