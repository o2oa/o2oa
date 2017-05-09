package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.exception.PromptException;
import com.x.base.core.http.EffectivePerson;

class ExceptionDenyEditTopCompany extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyEditTopCompany(EffectivePerson effectivePerson) {
		super("{} 权限不足,不能编辑顶层公司.", effectivePerson.getName());
	}
}
