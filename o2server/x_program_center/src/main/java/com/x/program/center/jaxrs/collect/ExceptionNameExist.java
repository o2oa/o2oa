package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameExist extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNameExist(String name) {
		super("用户:{}已注册.",Objects.toString(name) );
	}
}
