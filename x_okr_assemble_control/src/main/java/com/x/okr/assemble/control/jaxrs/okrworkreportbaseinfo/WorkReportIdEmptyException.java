package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportIdEmptyException() {
		super("工作汇报ID为空。" );
	}
}
