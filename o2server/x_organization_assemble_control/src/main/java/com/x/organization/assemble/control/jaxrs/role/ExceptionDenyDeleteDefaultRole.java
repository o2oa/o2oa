package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDenyDeleteDefaultRole extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteDefaultRole(String name) {
		super("不能删除系统默认角色:{}.", name);
	}
}
