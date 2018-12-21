package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportProcessLogSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportProcessLogSave( Throwable e ) {
		super("工作汇报处理人信息保存时发生异常。", e );
	}
}
