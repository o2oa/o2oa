package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyCreatePerson extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyCreatePerson(EffectivePerson effectivePerson, String name) {
		super("{} 不能创建个人:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
