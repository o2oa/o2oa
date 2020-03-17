package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionRedirectUriEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRedirectUriEmpty() {
		super("redirect_uri can not be empty.");
	}
}
