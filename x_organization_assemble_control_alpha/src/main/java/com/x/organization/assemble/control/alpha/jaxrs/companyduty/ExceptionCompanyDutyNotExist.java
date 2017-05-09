package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

import com.x.base.core.exception.PromptException;

class ExceptionCompanyDutyNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCompanyDutyNotExist(String flag) {
		super("公司职务:{}, 不存在.", flag);
	}
}
