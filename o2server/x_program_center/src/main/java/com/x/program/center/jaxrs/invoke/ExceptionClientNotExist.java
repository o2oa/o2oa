package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.exception.PromptException;

class ExceptionClientNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionClientNotExist(String client) {
		super("{} sso 配置不存在.", client);
	}
}
