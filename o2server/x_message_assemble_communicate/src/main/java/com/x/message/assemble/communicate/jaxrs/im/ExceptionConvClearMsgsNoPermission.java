package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConvClearMsgsNoPermission extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionConvClearMsgsNoPermission() {
		super("没有权限清空聊天记录！");
	}


}
