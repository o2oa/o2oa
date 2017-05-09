package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.exception.PromptException;

public class WorkTypeConfigIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkTypeConfigIdEmptyException() {
		super("id为空，无法进行查询操作。");
	}
}
