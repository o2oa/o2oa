package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class NameEmptyException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	NameEmptyException() {
		super("名称不能为空.");
	}
}
