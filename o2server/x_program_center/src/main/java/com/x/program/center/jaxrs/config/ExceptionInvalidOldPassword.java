package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInvalidOldPassword extends LanguagePromptException {

	private static final long serialVersionUID = -9024704466819643505L;

	public ExceptionInvalidOldPassword() {
		super("原密码错误.");
	}
}
