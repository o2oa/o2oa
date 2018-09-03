package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportInfoProcess( Throwable e, String message ) {
		super( message , e );
	}
}
