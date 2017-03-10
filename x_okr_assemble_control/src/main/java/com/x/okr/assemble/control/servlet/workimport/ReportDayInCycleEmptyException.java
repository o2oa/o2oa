package com.x.okr.assemble.control.servlet.workimport;

import com.x.base.core.exception.PromptException;

class ReportDayInCycleEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportDayInCycleEmptyException() {
		super("每周汇报日为空，无法继续保存工作信息!" );
	}
}
