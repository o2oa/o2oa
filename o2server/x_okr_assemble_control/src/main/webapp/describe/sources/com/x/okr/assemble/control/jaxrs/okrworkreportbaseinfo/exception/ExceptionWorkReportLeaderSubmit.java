package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportLeaderSubmit extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportLeaderSubmit( Throwable e, String id ) {
		super("工作汇报信息领导批阅处理时发生异常。ReportId：" + id, e );
	}
}
