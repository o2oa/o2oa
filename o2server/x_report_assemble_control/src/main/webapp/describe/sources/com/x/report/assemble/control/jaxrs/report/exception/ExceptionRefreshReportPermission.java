package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRefreshReportPermission extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRefreshReportPermission( Throwable e, String id ) {
		super("系统根据汇报ID更新汇报的访问权限信息时发生异常.reportId:" + id , e );
	}
}
