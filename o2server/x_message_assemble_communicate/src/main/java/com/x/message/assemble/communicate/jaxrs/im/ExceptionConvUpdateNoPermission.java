package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConvUpdateNoPermission extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionConvUpdateNoPermission() {
		super("没有权限修改会话属性");
	}


}
