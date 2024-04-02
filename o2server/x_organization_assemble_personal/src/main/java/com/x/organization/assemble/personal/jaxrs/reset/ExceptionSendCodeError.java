package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

class ExceptionSendCodeError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "验证码已下发，如未收到，请确认是否已绑定该号码.";

	ExceptionSendCodeError() {
		super(defaultMessage);
	}

	ExceptionSendCodeError(Throwable cause) {
		super(cause, defaultMessage);
	}
}
