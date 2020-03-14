package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgEmptyBody extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMsgEmptyBody() {
		super("消息内容不能为空.");
	}
}
