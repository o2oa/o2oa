package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportProcessLogIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportProcessLogIdEmpty() {
		super("工作汇报处理人ID为空。" );
	}
}
