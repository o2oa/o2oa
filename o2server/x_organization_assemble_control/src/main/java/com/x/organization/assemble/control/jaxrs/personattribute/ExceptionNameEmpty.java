package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameEmpty() {
		super("个人属性名称不能为空.");
	}
}
