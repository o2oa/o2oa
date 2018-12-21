package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportWorkflowStart extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportWorkflowStart( Throwable e, String profileId ) {
		super( "系统在为指定的概要文件启动汇报文档工作流时发生异常。profile:" + profileId , e );
	}
}
