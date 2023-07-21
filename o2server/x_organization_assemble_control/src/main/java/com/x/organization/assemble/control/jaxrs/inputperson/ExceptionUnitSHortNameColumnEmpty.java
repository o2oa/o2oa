package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitSHortNameColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionUnitSHortNameColumnEmpty() {
		super("组织代字列不能为空.");
	}
}
