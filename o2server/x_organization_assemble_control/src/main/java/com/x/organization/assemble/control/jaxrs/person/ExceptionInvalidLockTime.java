package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidLockTime extends PromptException {

	private static final long serialVersionUID = -551429161489167527L;

	ExceptionInvalidLockTime() {
		super("锁定到期时间必须大于当前时间.");
	}
}
