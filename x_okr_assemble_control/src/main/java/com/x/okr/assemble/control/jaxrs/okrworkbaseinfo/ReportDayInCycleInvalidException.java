package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class ReportDayInCycleInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportDayInCycleInvalidException( int cycle ) {
		super("每周汇报日选择不正确："+ cycle +"，无法继续保存工作信息!" );
	}
}
