package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionStartWorkflowForReport extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionStartWorkflowForReport( Throwable e, String id ) {
		super("系统根据汇报概要文件ID启动汇报审核流程时发生异常.profileId:" + id , e );
	}
}
