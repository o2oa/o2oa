package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionUniqueColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionUniqueColumnEmpty() {
		super("人员编号列不能为空.");
	}
}
