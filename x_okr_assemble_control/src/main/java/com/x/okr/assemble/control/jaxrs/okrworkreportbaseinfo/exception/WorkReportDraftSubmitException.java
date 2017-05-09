package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportDraftSubmitException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportDraftSubmitException( Throwable e ) {
		super("系统在提交工作汇报草稿信息时发生异常.", e );
	}
}
