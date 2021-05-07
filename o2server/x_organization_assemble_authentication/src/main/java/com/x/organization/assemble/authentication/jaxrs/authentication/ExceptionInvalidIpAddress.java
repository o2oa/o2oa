package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidIpAddress extends LanguagePromptException {

	private static final long serialVersionUID = -4915257511363100070L;

	public static String defaultMessage = "客户端IP限制，当前IP：{}.";

	ExceptionInvalidIpAddress(String ip) {
		super(defaultMessage, ip);
	}
}
