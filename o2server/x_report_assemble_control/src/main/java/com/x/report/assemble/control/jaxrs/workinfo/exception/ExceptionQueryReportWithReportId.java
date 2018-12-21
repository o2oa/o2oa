package com.x.report.assemble.control.jaxrs.workinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryReportWithReportId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryReportWithReportId( Throwable e, String id ) {
		super("系统根据汇报ID查询汇报基础信息时发生异常.reportId:" + id , e );
	}
}
