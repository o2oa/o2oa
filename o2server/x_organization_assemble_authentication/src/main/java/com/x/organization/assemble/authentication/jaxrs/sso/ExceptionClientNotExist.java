package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionClientNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static String defaultMessage = "{} sso 配置不存在.";

	ExceptionClientNotExist(String client) {
		super(defaultMessage, client);
	}
}
