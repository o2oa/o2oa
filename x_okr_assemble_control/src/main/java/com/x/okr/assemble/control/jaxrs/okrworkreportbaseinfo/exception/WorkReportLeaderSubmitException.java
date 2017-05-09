package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportLeaderSubmitException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportLeaderSubmitException( Throwable e, String id ) {
		super("工作汇报信息领导批阅处理时发生异常。ReportId：" + id, e );
	}
}
