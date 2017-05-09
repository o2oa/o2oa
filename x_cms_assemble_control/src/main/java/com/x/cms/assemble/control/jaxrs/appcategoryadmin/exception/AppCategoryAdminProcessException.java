package com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryAdminProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryAdminProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
