package com.x.report.assemble.control.jaxrs.workplan.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSaveWorkPlanNext extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSaveWorkPlanNext( Throwable e ) {
		super("系统在保存汇报下周期工作计划信息内容时发生异常。", e );
	}
}
