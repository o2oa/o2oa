package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyId extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyId() {
		super("Id不能为空");
	}


}
