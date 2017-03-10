package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.exception.PromptException;

class WorkChatIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkChatIdEmptyException() {
		super("id为空，无法进行查询操作。" );
	}
}
