package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class ReplySubjectIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReplySubjectIdEmptyException() {
		super("主题ID为空，无法继续查询操作。" );
	}
}
