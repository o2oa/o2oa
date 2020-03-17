package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgEmptyConversationId extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMsgEmptyConversationId() {
		super("会话ID不能为空.");
	}
}
