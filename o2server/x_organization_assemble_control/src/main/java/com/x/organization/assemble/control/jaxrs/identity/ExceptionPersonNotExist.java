package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonNotExist(String flag) {
		super("人员:{}, 不存在.", flag);
	}
}
