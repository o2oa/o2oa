package com.x.ai.assemble.control.jaxrs.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionCustom extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCustom(String msg) {
		super(msg);
	}
}
