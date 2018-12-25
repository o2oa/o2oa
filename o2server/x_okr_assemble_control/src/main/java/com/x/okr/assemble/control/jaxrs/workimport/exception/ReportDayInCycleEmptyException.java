package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class ReportDayInCycleEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReportDayInCycleEmptyException() {
		super("每周汇报日为空，无法继续保存工作信息!" );
	}
}
