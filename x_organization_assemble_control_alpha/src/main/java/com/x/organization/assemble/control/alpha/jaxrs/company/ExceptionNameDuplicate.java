package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.exception.PromptException;

class ExceptionNameDuplicate extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameDuplicate(String name) {
		super("公司名字已存在: {}.", name);
	}
}
