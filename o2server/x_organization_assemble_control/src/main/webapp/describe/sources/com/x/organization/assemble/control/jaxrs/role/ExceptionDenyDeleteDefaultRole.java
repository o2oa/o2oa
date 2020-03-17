package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyDeleteDefaultRole extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteDefaultRole(String name) {
		super("不能删除系统默认角色:{}.", name);
	}
}
