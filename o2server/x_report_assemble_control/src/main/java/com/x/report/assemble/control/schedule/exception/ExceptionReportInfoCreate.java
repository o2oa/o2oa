package com.x.report.assemble.control.schedule.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportInfoCreate extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportInfoCreate( Throwable e, String message ) {
		super( message, e );
	}
}
