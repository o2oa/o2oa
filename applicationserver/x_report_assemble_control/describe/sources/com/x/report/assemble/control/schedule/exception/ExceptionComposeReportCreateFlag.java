package com.x.report.assemble.control.schedule.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionComposeReportCreateFlag extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionComposeReportCreateFlag( Throwable e, String message ) {
		super( message, e );
	}
}
