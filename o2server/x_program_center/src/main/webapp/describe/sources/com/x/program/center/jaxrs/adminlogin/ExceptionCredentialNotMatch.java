package com.x.program.center.jaxrs.adminlogin;

import com.x.base.core.project.exception.PromptException;

class ExceptionCredentialNotMatch extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCredentialNotMatch() {
		super("身份不匹配.");
	}
}
