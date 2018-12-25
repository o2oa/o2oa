package com.x.cms.assemble.control.jaxrs.form.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionServiceLogic extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionServiceLogic( String message ) {
		super( message );
	}
	
	public ExceptionServiceLogic( Throwable e, String message ) {
		super( message, e );
	}
}
