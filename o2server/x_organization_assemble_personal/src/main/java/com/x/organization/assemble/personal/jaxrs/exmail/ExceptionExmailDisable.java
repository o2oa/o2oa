package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * 
 * @author ray
 *
 */
class ExceptionExmailDisable extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionExmailDisable() {
		super("没有启用腾讯企业邮.");
	}
}
