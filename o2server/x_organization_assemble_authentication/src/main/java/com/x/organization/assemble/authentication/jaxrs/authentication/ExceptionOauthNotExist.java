package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "无法找到名为:{} 的OauthClient登录配置.";

	ExceptionOauthNotExist(String name) {
		super(defaultMessage, name);
	}
}
