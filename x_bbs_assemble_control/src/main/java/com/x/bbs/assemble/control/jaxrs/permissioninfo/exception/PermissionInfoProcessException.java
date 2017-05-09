package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.exception.PromptException;

public class PermissionInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PermissionInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
