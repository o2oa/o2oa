package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class UnableConnectException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	UnableConnectException() {
		super("无法连接到外网注册服务器,请检查服务器是否可以正常连接到Internet网络,DNS解析是否正常.");
	}
}
