package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.exception.PromptException;

public class ReadProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReadProcessException( Throwable e, String id ) {
		super("系统在处理待阅信息时发生异常。 ID:" + id );
	}
}
