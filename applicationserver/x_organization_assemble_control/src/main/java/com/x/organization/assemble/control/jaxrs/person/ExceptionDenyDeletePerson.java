package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyDeletePerson extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeletePerson(EffectivePerson effectivePerson, String name) {
		super("{} 不能删除个人:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
