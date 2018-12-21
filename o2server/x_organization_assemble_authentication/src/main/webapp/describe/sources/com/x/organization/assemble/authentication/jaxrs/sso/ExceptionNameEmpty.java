package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameEmpty() {
		super("名称为空.");
	}
}
