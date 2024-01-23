package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionFailureLocked extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "您已经被锁定, 解锁时间： {} .";

	ExceptionFailureLocked(String time) {
		super(defaultMessage, time);
	}
}
