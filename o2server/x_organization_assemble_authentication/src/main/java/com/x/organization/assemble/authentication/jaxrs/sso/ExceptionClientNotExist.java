package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionClientNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionClientNotExist(String client) {
		super("{} sso 配置不存在.", client);
	}
}
