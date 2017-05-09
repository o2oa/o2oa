package com.x.organization.assemble.control.alpha.jaxrs.companyattribute;

import com.x.base.core.exception.PromptException;

class ExceptionCompanyAttributeNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCompanyAttributeNotExist(String flag) {
		super("公司属性:{}, 不存在.", flag);
	}
}
