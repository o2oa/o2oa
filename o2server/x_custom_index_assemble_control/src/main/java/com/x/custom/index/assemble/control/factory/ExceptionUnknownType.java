package com.x.custom.index.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnknownType extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionUnknownType(String type) {
		super("unknown type:{}.", type);
	}
}
