package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionIdNumberColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionIdNumberColumnEmpty() {
		super("身份证号列不能为空.");
	}
}
