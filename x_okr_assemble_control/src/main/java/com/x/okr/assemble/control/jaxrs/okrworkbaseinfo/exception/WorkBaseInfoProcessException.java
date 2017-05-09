package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkBaseInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkBaseInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
