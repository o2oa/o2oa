package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionSendCodeError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "发送失败.";

	ExceptionSendCodeError() {
		super(defaultMessage);
	}

	ExceptionSendCodeError(Throwable cause) {
		super(cause, defaultMessage);
	}
}
