package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyCreateTopUnit extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyCreateTopUnit(EffectivePerson effectivePerson, String name) {
		super("{} 不能创建顶层组织:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
