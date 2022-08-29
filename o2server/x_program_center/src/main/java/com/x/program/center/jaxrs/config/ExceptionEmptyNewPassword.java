package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionEmptyNewPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionEmptyNewPassword() {
		super("新密码不能为空.");
	}
}
