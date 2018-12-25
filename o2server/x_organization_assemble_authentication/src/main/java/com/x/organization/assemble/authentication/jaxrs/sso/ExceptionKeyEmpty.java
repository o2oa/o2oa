package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionKeyEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionKeyEmpty() {
		super("sso 配置token不能为空.");
	}
}
