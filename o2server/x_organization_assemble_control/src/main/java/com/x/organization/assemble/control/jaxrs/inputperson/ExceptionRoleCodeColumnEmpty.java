package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoleCodeColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionRoleCodeColumnEmpty() {
		super("角色编号列不能为空.");
	}
}
