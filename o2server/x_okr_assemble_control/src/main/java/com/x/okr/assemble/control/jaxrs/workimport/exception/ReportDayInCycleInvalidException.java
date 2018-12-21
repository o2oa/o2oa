package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class ReportDayInCycleInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReportDayInCycleInvalidException( int cycle ) {
		super("每周汇报日选择不正确："+ cycle +"，无法继续保存工作信息!" );
	}
}
