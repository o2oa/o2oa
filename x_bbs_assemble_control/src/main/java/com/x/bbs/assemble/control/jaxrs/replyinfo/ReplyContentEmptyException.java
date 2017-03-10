package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyContentEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyContentEmptyException() {
		super("回复内容为空，无法继续保存操作。" );
	}
}
