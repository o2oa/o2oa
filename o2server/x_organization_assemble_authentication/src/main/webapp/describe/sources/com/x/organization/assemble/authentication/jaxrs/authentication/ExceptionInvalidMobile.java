package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidMobile extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidMobile(String mobile) {
		super("手机号 {} 错误,不能为空,且必须符合指定格式.", mobile);
	}
}
