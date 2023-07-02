package com.x.organization.assemble.authentication.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindPerson extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCanNotFindPerson(String userId) {
		super("无法绑定政务钉钉用户:{}.", userId);
	}
}
