package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthNotExist(String name) {
		super("无法找到名为:{} 的OauthClient登录配置.", name);
	}
}
