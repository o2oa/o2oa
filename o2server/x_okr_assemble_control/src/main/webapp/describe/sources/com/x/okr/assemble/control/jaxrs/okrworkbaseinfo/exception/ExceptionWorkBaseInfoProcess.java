package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkBaseInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkBaseInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
	public ExceptionWorkBaseInfoProcess( String message ) {
		super( message );
	}
}
