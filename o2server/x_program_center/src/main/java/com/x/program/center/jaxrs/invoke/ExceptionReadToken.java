package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionReadToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionReadToken(String client, String token) {
		super("无法读取sso令牌，客户端:{}，令牌:{}.", client, token);
	}
}
