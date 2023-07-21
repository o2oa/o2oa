package com.x.cms.assemble.control.jaxrs.form;

import com.x.base.core.project.exception.PromptException;

class ExceptionServiceLogic extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionServiceLogic( String message ) {
		super( message );
	}
	
	ExceptionServiceLogic( Throwable e, String message ) {
		super( message, e );
	}
}
