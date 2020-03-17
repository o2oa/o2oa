package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyToIdentity extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionEmptyToIdentity() {
		super("被委托身份为空.");
	}

}
