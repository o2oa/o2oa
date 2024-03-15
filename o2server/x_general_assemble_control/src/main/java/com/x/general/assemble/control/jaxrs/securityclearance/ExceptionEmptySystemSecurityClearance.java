package com.x.general.assemble.control.jaxrs.securityclearance;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptySystemSecurityClearance extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionEmptySystemSecurityClearance() {
		super("系统密级标识不能为空.");
	}
}
