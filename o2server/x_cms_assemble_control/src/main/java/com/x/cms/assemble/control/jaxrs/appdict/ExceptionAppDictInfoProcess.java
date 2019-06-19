package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppDictInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppDictInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
}
