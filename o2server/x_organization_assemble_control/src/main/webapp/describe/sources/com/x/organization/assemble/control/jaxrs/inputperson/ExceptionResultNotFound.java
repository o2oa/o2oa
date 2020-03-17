package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionResultNotFound extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionResultNotFound(String flag) {
		super("找不到导入结果:{}.", flag);
	}
}
