package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionEntityPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionEntityPropertyEmpty(String message) {
		super("信息属性为空！MESSAGE：" + message );
	}
}
