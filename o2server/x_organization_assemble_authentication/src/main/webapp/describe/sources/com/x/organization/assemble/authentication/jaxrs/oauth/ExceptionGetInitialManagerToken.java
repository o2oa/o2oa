package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetInitialManagerToken extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGetInitialManagerToken() {
		super("不能通过oauth获取初始管理员的token.");
	}
}
