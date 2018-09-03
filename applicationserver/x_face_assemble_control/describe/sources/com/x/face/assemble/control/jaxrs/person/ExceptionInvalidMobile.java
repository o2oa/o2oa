package com.x.face.assemble.control.jaxrs.person;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidMobile extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidMobile(String name) {
		super("手机号码不合法:" + Objects.toString(name) + ".");
	}
}
