package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyParameter extends PromptException {


	private static final long serialVersionUID = -4002941653651110788L;

	ExceptionEmptyParameter(String name) {
		super(name + "不能为空.");
	}
}
