package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionTypeCodeColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionTypeCodeColumnEmpty() {
		super("级别编号列不能为空.");
	}
}
