package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonNotExist(String flag) {
		super("用户:{}不存在.", flag);
	}
}
