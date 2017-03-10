package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplySubjectIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplySubjectIdEmptyException() {
		super("主题ID为空，无法继续查询操作。" );
	}
}
