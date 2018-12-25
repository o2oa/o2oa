package com.x.cms.assemble.control.jaxrs.appdict.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAppDictInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAppDictInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
}
