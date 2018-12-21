package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionReadToken extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionReadToken(String client, String token) {
		super("can not read sso token, client:{}, token:{}.", client, token);
	}
}
