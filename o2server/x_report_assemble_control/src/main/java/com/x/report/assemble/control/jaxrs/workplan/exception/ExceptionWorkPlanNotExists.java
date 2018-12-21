package com.x.report.assemble.control.jaxrs.workplan.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkPlanNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkPlanNotExists( String id ) {
		super("指定的工作计划信息不存在.reportId:" + id );
	}
}
