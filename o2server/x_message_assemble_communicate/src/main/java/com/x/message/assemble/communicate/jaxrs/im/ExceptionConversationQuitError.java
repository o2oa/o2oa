package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConversationQuitError extends PromptException {


	private static final long serialVersionUID = 8192689860325401533L;

	ExceptionConversationQuitError(String msg) {
		super("错误：{}", msg);
	}
}
