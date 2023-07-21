package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionSecretNotMatch extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSecretNotMatch(String client_id, String client_secret) {
		super("客户标识:{}, 与密钥:{}, 不匹配.", client_id, client_secret);
	}
}
