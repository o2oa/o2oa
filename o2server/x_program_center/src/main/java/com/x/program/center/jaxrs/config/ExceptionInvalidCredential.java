package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInvalidCredential extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInvalidCredential() {
		super("不正确的用户标识.");
	}
}
