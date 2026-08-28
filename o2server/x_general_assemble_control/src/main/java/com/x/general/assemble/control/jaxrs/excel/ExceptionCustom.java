package com.x.general.assemble.control.jaxrs.excel;

import com.x.base.core.project.exception.PromptException;

class ExceptionCustom extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCustom(String msg) {
		super(msg);
	}
}
