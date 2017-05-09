package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class RoleInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public RoleInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
