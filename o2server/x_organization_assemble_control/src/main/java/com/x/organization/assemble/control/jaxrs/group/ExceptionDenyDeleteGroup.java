package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyDeleteGroup extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDenyDeleteGroup(EffectivePerson effectivePerson, String name) {
		super("{}不能删除群组:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
