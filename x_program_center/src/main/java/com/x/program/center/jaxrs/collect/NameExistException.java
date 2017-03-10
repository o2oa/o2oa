package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.exception.PromptException;

class NameExistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NameExistException(String name) {
		super("用户:" + Objects.toString(name) + "已注册.");
	}
}
