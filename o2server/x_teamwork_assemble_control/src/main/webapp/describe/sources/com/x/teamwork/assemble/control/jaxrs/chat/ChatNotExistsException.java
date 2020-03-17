package com.x.teamwork.assemble.control.jaxrs.chat;

import com.x.base.core.project.exception.PromptException;

class ChatNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ChatNotExistsException( String id ) {
		super("指定ID的工作交流信息不存在。ID:" + id );
	}
}
