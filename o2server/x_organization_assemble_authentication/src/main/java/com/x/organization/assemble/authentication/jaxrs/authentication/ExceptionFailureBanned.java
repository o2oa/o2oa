package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionFailureBanned extends PromptException {

	private static final long serialVersionUID = -2174848875972265963L;

	public static String defaultMessage = "您的账号已被禁止访问，请联系管理员.";

	ExceptionFailureBanned() {
		super(defaultMessage);
	}
}
