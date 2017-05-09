package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class ReplyIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReplyIdEmptyException() {
		super("id为空，无法继续查询操作。" );
	}
}
