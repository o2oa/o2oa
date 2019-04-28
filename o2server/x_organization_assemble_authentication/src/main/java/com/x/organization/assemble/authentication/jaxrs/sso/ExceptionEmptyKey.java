package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyKey extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyKey() {
		super("sso 配置token不能为空.");
	}
}
