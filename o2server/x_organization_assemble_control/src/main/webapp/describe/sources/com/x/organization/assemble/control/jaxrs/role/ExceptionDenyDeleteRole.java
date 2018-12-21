package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyDeleteRole extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteRole(EffectivePerson effectivePerson, String name) {
		super("{} 不能删除角色:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
