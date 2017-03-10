package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import com.x.base.core.exception.PromptException;

class TaskHandledIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskHandledIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
