package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitTypeCodeColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionUnitTypeCodeColumnEmpty() {
		super("上级组织编号不能为空.");
	}
}
