package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectOperation extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectOperation( Throwable e, String message ) {
		super( message, e );
	}
}
