package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgRevokeNoPermission extends PromptException {


	private static final long serialVersionUID = 126285286239527769L;

	ExceptionMsgRevokeNoPermission() {
		super("当前用户没有权限撤回消息");
	}


}
