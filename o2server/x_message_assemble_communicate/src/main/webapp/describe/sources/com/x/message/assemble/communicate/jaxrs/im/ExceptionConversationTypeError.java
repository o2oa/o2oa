package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConversationTypeError extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionConversationTypeError() {
		super("会话类型不能为空或类型不正确.");
	}
}
