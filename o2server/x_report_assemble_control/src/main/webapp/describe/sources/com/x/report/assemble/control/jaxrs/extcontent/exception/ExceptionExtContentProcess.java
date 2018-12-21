package com.x.report.assemble.control.jaxrs.extcontent.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionExtContentProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionExtContentProcess( Throwable e, String message ) {
		super( message , e );
	}
}
