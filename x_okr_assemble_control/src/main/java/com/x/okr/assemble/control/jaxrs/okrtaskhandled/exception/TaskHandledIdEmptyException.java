package com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception;

import com.x.base.core.exception.PromptException;

public class TaskHandledIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public TaskHandledIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
