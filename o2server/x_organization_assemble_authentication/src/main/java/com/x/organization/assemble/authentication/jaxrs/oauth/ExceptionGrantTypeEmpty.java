package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionGrantTypeEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGrantTypeEmpty() {
		super("grant_type can not be empty.");
	}
}
