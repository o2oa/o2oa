package com.x.okr.assemble.control.jaxrs.appraise.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkAppraiseProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkAppraiseProcess(Throwable e, String message ) {
		super( message, e );
	}
}
