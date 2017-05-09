package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.exception.PromptException;

public class TaskIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public TaskIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
