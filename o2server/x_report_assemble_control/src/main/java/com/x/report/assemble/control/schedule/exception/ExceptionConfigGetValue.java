package com.x.report.assemble.control.schedule.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigGetValue extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigGetValue( Throwable e, String message ) {
		super( message, e );
	}
}
