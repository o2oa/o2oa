package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

import com.x.base.core.exception.PromptException;

class ReportProcessLogSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportProcessLogSaveException( Throwable e ) {
		super("工作汇报处理人信息保存时发生异常。", e );
	}
}
