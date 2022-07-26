package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionEmptyCredential extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionEmptyCredential() {
		super("用户标识不能为空.");
	}
}
