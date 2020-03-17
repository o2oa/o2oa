package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionTokenFlagEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionTokenFlagEmpty() {
		super("token中包含的unique不能为空.");
	}
}
