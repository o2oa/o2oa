package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoProcess( Throwable e, String message ) {
		super( message, e );
	}

	ExceptionCategoryInfoProcess( String message ) {
		super( message );
	}
}
