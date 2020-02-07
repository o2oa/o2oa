package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.exception.PromptException;

class ExceptionExmailDisable extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionExmailDisable() {
		super("没有启用腾讯企业邮.");
	}
}
