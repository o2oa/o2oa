package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthEmptyInfo extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthEmptyInfo() {
		super("请求信息返回值为空.");
	}
}
