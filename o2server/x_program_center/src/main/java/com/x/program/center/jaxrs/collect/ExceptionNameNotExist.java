package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNameNotExist(String name) {
		super("用户:{}不存在.",Objects.toString(name) );
	}
}
