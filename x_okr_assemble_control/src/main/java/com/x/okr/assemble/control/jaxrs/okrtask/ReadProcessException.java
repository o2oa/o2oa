package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class ReadProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReadProcessException( Throwable e, String id ) {
		super("系统在处理待阅信息时发生异常。 ID:" + id );
	}
}
