package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;

import com.x.base.core.exception.PromptException;

class ReportPersonLinkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportPersonLinkIdEmptyException() {
		super("工作汇报处理人ID为空。" );
	}
}
