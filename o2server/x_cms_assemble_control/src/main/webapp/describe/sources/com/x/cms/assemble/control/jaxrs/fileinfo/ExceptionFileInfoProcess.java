package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
	
	ExceptionFileInfoProcess( String message ) {
		super( message);
	}
}
