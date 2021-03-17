package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmailEmpty extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionEmailEmpty() {
		super("邮箱不能为空.");
	}
}
