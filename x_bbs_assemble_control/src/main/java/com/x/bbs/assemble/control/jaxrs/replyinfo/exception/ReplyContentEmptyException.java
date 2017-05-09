package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class ReplyContentEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReplyContentEmptyException() {
		super("回复内容为空，无法继续保存操作。" );
	}
}
