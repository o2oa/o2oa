package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyEditRole extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyEditRole(EffectivePerson effectivePerson, String name) {
		super("{} 不能编辑角色:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
