package com.x.cms.assemble.control.jaxrs.categoryinfo.exception;

import com.x.base.core.exception.PromptException;

public class CategoryInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CategoryInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
