package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportDayInCycleInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportDayInCycleInvalid( int cycle ) {
		super("每周汇报日选择不正确："+ cycle +"，无法继续保存工作信息!" );
	}
}
