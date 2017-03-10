package com.x.okr.assemble.control.servlet.task;

import com.x.base.core.exception.PromptException;

class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonNotExistsException( String flag ) {
		super("用户信息不存在!Flag:" + flag +".");
	}
}
