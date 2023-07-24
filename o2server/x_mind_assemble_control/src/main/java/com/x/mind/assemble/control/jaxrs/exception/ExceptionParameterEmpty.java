package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionParameterEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionParameterEmpty(String message) {
		super("参数不合法！MESSAGE：" + message );
	}
}
