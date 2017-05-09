package com.x.cms.assemble.control.jaxrs.documentpermission.exception;

import com.x.base.core.exception.PromptException;

public class ServiceLogicException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ServiceLogicException( String message ) {
		super( message );
	}
	
	public ServiceLogicException( Throwable e, String message ) {
		super( message, e );
	}
}
