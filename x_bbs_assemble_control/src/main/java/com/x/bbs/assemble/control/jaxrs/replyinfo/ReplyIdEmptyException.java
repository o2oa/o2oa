package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyIdEmptyException() {
		super("id为空，无法继续查询操作。" );
	}
}
