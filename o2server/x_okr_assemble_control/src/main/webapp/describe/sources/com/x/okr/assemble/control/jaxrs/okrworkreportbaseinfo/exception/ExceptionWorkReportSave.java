package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportSave( Throwable e ) {
		super("系统在保存工作汇报信息时发生异常.", e );
	}
}
