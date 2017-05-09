package com.x.organization.assemble.control.alpha.jaxrs.inputperson;

import com.x.base.core.exception.PromptException;

class MobileColumnEmptyException extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	MobileColumnEmptyException() {
		super("手机号列不能为空.");
	}
}
