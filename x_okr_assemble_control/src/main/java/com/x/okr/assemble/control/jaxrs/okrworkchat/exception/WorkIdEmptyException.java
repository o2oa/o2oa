package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.exception.PromptException;

public class WorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
