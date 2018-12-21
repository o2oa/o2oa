package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportAdminProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportAdminProcess( Throwable e, String id ) {
		super("工作汇报信息督办员处理时发生异常。ReportId：" + id, e );
	}
}
