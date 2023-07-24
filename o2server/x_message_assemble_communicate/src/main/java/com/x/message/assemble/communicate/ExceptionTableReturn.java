package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionTableReturn extends PromptException {

	private static final long serialVersionUID = 6235890108470383271L;

	ExceptionTableReturn(String id, String title, String table) {
		super("execute table message return is not true, message:{}, title:{}, table:{}.", id, title, table);
	}
}
