package com.x.organization.assemble.control.alpha.jaxrs.inputperson;

import com.x.base.core.exception.PromptException;

class NameColumnEmptyException extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	NameColumnEmptyException() {
		super("姓名列不能为空.");
	}
}
