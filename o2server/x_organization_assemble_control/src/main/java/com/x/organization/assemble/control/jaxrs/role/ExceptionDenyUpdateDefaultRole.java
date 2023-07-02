package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.PromptException;

class ExceptionDenyUpdateDefaultRole extends PromptException {

	private static final long serialVersionUID = 5167597034206987929L;

	ExceptionDenyUpdateDefaultRole(String name) {
		super("不能修改系统默认角色名称:{}.", name);
	}
}
