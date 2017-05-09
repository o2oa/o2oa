package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportSubmitException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportSubmitException( Throwable e, String id ) {
		super("系统在提交工作汇报信息时发生异常.ReportId:" + id, e );
	}
}
