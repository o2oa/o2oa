package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.exception.PromptException;

class NameNotExistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NameNotExistException(String name) {
		super("用户:" + Objects.toString(name) + "不存在.");
	}
}
