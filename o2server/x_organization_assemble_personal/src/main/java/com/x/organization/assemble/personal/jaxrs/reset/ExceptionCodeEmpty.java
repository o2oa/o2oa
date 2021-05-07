package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionCodeEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCodeEmpty() {
		super("短信验证码不能为空.");
	}
}
