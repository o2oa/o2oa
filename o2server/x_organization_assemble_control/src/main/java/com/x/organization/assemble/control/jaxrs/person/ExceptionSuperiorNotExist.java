package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionSuperiorNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionSuperiorNotExist(String superior) {
		super("指定的上级住主管: {} 不存在.", superior);
	}
}
