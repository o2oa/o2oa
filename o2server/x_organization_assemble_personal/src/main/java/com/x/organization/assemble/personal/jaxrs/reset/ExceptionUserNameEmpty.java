package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserNameEmpty  extends PromptException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ExceptionUserNameEmpty() {
		super("用户名不能为空.");
	}

}
