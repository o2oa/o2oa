package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.exception.PromptException;

public class SercretaryConfigIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SercretaryConfigIdEmptyException() {
		super("id为空，无法继续进行查询操作。");
	}
}
