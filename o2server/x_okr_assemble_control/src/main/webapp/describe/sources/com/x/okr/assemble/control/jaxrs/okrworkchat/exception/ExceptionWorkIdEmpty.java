package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkIdEmpty() {
		super("id为空，无法进行查询。" );
	}
}
