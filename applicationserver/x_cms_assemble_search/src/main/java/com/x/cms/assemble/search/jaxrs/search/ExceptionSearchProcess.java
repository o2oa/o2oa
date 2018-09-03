package com.x.cms.assemble.search.jaxrs.search;

import com.x.base.core.project.exception.PromptException;

class ExceptionSearchProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSearchProcess( Throwable e, String message ) {
		super( message, e );
	}
	
	ExceptionSearchProcess( String message ) {
		super( message);
	}
}
