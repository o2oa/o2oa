package com.x.organization.assemble.authentication.jaxrs.andfx;

import com.x.base.core.project.exception.PromptException;

class ExceptionAndFx extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAndFx(String errCode, String errMsg) {
		super("andFx error: errCode: {}, errMsg:{}.", errCode, errMsg);
	}
}
