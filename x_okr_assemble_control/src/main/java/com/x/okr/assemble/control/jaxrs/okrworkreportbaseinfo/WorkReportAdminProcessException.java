package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportAdminProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportAdminProcessException( Throwable e, String id ) {
		super("工作汇报信息处理时发生异常。ReportId：" + id, e );
	}
}
