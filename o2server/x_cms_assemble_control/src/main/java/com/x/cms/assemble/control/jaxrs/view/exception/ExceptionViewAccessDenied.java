package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionViewAccessDenied extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionViewAccessDenied( Throwable e, String message ) {
		super( message, e );
	}

	public ExceptionViewAccessDenied(String categoryId, String viewId) {
		super("category id: {} access view id: {} denied.", categoryId, viewId );
	}
}
