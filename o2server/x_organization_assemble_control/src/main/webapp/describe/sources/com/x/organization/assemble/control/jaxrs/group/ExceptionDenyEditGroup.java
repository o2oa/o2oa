package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyEditGroup extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDenyEditGroup(EffectivePerson effectivePerson, String name) {
		super("{} 不能编辑群组:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}