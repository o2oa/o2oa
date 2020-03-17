package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewInfoProcess( String message ) {
		super( message );
	}
	
	ExceptionViewInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
}
