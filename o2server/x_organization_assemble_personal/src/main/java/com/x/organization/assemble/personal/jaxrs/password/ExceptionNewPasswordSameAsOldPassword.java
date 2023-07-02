package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNewPasswordSameAsOldPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNewPasswordSameAsOldPassword() {
		super("新密码不能和旧密码相同.");
	}
}
