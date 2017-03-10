package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class TaskIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
