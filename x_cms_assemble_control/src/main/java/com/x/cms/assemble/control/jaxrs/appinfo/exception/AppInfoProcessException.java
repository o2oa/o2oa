package com.x.cms.assemble.control.jaxrs.appinfo.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
