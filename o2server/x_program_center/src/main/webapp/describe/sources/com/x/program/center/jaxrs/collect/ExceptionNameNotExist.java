package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNameNotExist(String name) {
		super("用户:" + Objects.toString(name) + "不存在.");
	}
}
