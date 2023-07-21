package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitUniqueColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionUnitUniqueColumnEmpty() {
		super("组织编号列不能为空.");
	}
}
