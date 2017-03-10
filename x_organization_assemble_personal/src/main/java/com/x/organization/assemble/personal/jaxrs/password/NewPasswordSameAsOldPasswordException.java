package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.exception.PromptException;

class NewPasswordSameAsOldPasswordException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NewPasswordSameAsOldPasswordException() {
		super("新密码不能和旧密码相同.");
	}
}
