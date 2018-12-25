package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidRemoteAddr extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionInvalidRemoteAddr(String addr, String name) {
		super("无效的访问地址: {}, 调用接口名: {}.", addr, name);
	}
}
