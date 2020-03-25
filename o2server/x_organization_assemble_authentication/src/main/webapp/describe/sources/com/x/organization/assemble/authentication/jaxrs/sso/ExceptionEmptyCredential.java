package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyCredential extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyCredential() {
		super("名称为空.");
	}
}
