package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionSingleConvNotUpdate extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSingleConvNotUpdate() {
		super("单聊会话无法修改属性");
	}


}
