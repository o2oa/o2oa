package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class DisableException extends PromptException {

	private static final long serialVersionUID = 9107373129400635015L;

	DisableException() {
		super("没有启用连接到注册服务器功能.");
	}
}
