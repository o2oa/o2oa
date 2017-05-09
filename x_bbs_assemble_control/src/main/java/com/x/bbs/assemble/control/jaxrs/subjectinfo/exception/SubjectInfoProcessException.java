package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class SubjectInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SubjectInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
