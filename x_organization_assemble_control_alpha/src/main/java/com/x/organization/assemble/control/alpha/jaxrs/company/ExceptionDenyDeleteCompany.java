package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.exception.PromptException;
import com.x.base.core.http.EffectivePerson;

class ExceptionDenyDeleteCompany extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteCompany(EffectivePerson effectivePerson, String name) {
		super("{} 不能删除公司:{}, 权限不足.", effectivePerson.getName(), name);
	}
}
