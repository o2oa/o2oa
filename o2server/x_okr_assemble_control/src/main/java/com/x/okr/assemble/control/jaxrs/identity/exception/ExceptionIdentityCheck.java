package com.x.okr.assemble.control.jaxrs.identity.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionIdentityCheck extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionIdentityCheck( Throwable e, String identity ) {
		super("身份信息检查时发生异常。identity:" + identity, e );
	}
}
