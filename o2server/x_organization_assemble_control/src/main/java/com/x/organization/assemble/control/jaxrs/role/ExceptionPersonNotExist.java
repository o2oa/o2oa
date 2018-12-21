package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonNotExist(String flag) {
		super("人员:{}, 不存在.", flag);
	}
}
