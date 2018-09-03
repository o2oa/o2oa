package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyEditPerson extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyEditPerson(EffectivePerson effectivePerson, String name) {
		super("{} 不能编辑个人:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
