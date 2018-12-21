package com.x.program.center.jaxrs.adminlogin;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPasswordEmpty() {
		super("口令不能为空.");
	}
}
