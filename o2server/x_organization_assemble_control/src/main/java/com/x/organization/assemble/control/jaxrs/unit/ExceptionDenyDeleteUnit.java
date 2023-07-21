package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyDeleteUnit extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteUnit(EffectivePerson effectivePerson, String name) {
		super("{} 不能删除组织:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
