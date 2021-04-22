package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyCreateRole extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyCreateRole(EffectivePerson effectivePerson, String name) {
		super("{} 不能创建角色:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
