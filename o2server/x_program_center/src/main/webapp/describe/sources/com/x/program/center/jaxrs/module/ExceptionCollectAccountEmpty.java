package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionCollectAccountEmpty extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionCollectAccountEmpty() {
		super("配置表中登录到云服务器帐号为空或者密码为空.");
	}
}
