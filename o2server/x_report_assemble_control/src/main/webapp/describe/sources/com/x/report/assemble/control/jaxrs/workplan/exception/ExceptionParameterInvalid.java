package com.x.report.assemble.control.jaxrs.workplan.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionParameterInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionParameterInvalid( String message ) {
		super("传入的参数不合法." + message );
	}
}
