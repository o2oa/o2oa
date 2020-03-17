package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPasswordEmpty() {
		super("密码不能为空.");
	}
}
