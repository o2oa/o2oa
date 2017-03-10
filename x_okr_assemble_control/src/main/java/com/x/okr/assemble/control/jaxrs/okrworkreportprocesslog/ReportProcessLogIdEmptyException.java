package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

import com.x.base.core.exception.PromptException;

class ReportProcessLogIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportProcessLogIdEmptyException() {
		super("工作汇报处理人ID为空。" );
	}
}
