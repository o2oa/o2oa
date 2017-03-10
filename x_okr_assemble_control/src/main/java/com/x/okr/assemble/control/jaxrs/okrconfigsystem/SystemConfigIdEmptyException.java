package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.exception.PromptException;

class SystemConfigIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SystemConfigIdEmptyException() {
		super("id为空，无法进行查询操作。");
	}
}
