package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionAdmin extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAdmin() {
		super("can not sso admin.");
	}
}
