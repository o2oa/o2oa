package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionClientNotEnable extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static final String DEFAULT_MESSAGE = "{} sso 配置未启用.";

	ExceptionClientNotEnable(String client) {
		super(DEFAULT_MESSAGE, client);
	}
}
