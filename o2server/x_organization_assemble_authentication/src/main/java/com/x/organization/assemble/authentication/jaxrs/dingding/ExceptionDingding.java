package com.x.organization.assemble.authentication.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingding extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDingding(Integer errCode, String errMsg) {
		super("ding ding error: errCode: {}, errMsg:{}.", errCode, errMsg);
	}
}
