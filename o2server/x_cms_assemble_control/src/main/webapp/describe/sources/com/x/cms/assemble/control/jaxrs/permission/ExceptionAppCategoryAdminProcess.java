package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppCategoryAdminProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppCategoryAdminProcess( Throwable e, String message ) {
		super( message, e );
	}
}
