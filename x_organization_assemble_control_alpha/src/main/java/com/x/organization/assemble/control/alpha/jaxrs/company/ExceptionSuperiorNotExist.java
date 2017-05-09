package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.exception.PromptException;

class ExceptionSuperiorNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSuperiorNotExist(String flag, String superiorFlag) {
		super("公司:{}, 的上级公司: {}, 不存在.", flag, superiorFlag);
	}
}
