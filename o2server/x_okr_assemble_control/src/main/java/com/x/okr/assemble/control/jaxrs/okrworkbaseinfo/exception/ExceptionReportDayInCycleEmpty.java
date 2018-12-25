package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportDayInCycleEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportDayInCycleEmpty() {
		super("每周汇报日为空，无法继续保存工作信息!" );
	}
}
