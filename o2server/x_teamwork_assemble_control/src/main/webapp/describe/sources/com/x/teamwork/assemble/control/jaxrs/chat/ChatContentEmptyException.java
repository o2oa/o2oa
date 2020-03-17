package com.x.teamwork.assemble.control.jaxrs.chat;

import com.x.base.core.project.exception.PromptException;

class ChatContentEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ChatContentEmptyException() {
		super("工作交流内容为空。" );
	}
}
