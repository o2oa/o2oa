package com.x.program.center.jaxrs.adminlogin;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordNotMatch extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPasswordNotMatch() {
		super("口令不匹配.");
	}
}
