package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConversationNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionConversationNotExist() {
		super("会话不存在！");
	}
}
