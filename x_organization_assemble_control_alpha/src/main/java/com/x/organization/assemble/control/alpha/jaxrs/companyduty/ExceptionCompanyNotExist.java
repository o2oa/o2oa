package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

import com.x.base.core.exception.PromptException;

class ExceptionCompanyNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCompanyNotExist(String flag) {
		super("公司:{}, 不存在.", flag);
	}
}
