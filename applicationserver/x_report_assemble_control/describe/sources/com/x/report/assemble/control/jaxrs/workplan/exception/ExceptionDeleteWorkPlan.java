package com.x.report.assemble.control.jaxrs.workplan.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeleteWorkPlan extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDeleteWorkPlan( Throwable e, String id ) {
		super("系统根据工作计划ID删除工作计划信息时发生异常.id:" + id , e );
	}
}
