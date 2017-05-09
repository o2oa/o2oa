package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.exception.PromptException;

public class SystemConfigIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SystemConfigIdEmptyException() {
		super("id为空，无法进行查询操作。");
	}
}
