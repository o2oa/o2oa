package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionViewInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionViewInfoProcess( String message ) {
		super( message );
	}
	
	public ExceptionViewInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
}
