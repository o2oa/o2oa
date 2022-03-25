package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyCredential extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyCredential() {
		super("名称为空.");
	}
}
