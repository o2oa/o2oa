package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionCodeEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCodeEmpty() {
		super("企业微信登录code不能为空.");
	}
}
