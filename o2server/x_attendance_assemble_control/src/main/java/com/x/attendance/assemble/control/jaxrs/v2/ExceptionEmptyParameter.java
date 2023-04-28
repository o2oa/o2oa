package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

public class ExceptionEmptyParameter extends PromptException {


	private static final long serialVersionUID = -4002941653651110788L;

	public ExceptionEmptyParameter(String name) {
		super(name + "不能为空.");
	}
}
