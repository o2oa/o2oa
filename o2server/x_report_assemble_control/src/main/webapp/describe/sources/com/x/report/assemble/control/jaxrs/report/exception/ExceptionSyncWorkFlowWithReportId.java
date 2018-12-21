package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSyncWorkFlowWithReportId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSyncWorkFlowWithReportId( Throwable e, String id ) {
		super("系统根据指定汇报ID同步汇报审批流程信息容时发生异常.reportId:" + id , e );
	}
}
