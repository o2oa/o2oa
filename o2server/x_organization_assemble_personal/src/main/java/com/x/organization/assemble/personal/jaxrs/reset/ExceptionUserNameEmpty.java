package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionUserNameEmpty  extends LanguagePromptException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	ExceptionUserNameEmpty() {
		super("用户名不能为空.");
	}

}
