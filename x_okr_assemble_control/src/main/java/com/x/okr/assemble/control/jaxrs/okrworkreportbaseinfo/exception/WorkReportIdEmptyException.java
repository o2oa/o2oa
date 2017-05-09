package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportIdEmptyException() {
		super("工作汇报ID为空。" );
	}
}
