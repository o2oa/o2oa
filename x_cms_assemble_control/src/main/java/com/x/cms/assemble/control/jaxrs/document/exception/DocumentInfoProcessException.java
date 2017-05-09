package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class DocumentInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DocumentInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
