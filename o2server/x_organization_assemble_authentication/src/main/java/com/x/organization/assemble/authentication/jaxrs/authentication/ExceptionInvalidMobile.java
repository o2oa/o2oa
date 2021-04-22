package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidMobile extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "手机号 {} 错误,不能为空,且必须符合指定格式.";

	ExceptionInvalidMobile(String mobile) {
		super(defaultMessage, mobile);
	}
}
