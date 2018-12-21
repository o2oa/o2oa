package com.x.report.assemble.control.jaxrs.workplan.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryWorkPlanWithReportId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryWorkPlanWithReportId( Throwable e, String id ) {
		super("系统根据汇报ID查询汇报工作计划信息列表时发生异常.reportId:" + id , e );
	}
}
