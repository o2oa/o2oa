package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyBusinessType extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyBusinessType() {
		super("businessType 为空或内容错误！");
	}


}
