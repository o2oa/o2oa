package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.exception.PromptException;

class ExceptionIdDuplicate extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionIdDuplicate(String id) {
		super("公司Id已存在: {}.", id);
	}
}
