package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;

import com.x.base.core.exception.PromptException;

class ReportDetailIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportDetailIdEmptyException() {
		super("工作汇报ID为空。" );
	}
}
