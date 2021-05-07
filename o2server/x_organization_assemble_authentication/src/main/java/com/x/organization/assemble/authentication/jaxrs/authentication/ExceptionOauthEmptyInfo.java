package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthEmptyInfo extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "请求信息返回值为空.";

	ExceptionOauthEmptyInfo() {
		super(defaultMessage);
	}
}
