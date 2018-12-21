package com.x.report.assemble.control.jaxrs.workplan.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkPlanNextNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkPlanNextNotExists( String id ) {
		super("指定的下周期工作计划信息不存在.reportId:" + id );
	}
}
