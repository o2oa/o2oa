package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionClientIdEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionClientIdEmpty() {
		super("client_id can not be empty.");
	}
}
