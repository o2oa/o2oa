package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportProcessLogList extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportProcessLogList( Throwable e, String id ) {
		super("系统根据指定工作汇报ID查询汇报所有的处理记录列表时发生异常。ReportId：" + id, e );
	}
}
