package com.x.cms.assemble.search.jaxrs.spider;

import com.x.base.core.project.exception.PromptException;

class ExceptionSpiderProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSpiderProcess( Throwable e, String message ) {
		super( message, e );
	}
	
	ExceptionSpiderProcess( String message ) {
		super( message);
	}
}
