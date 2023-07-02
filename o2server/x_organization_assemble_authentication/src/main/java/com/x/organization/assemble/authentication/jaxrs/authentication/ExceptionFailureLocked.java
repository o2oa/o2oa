package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFailureLocked extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "用户:{} 已经被锁定, 锁定时间 {} 分钟.";

	ExceptionFailureLocked(String name, Integer minutes) {
		super(defaultMessage, name, minutes);
	}
}
