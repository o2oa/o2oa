package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewAccessDenied extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewAccessDenied( Throwable e, String message ) {
		super( message, e );
	}

	ExceptionViewAccessDenied(String categoryId, String viewId) {
		super("category id: {} access view id: {} denied.", categoryId, viewId );
	}
}
