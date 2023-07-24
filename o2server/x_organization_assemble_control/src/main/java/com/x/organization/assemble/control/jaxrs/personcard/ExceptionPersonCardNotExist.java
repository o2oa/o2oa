package com.x.organization.assemble.control.jaxrs.personcard;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonCardNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonCardNotExist(String flag) {
		super("个人名片:{}, 不存在.", flag);
	}
}
