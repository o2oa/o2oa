package com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTaskHandledIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTaskHandledIdEmpty() {
		super("id为空，无法进行查询。" );
	}
}
