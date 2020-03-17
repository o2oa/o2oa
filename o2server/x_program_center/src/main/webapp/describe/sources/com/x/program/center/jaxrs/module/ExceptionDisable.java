package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionDisable extends PromptException {

	private static final long serialVersionUID = 9107373129400635015L;

	ExceptionDisable() {
		super("没有启用连接到注册服务器功能.");
	}
}
