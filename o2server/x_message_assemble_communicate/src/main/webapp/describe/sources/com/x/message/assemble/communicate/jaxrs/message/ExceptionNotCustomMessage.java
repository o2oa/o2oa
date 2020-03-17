package com.x.message.assemble.communicate.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotCustomMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNotCustomMessage(String type) {
		super("无法接收非定制内容:{}.", type);
	}
}
