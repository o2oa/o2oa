package com.x.okr.assemble.control.jaxrs.identity.exception;

import com.x.base.core.exception.PromptException;

public class IdentityCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public IdentityCheckException( Throwable e, String identity ) {
		super("身份信息检查时发生异常。identity:" + identity, e );
	}
}
