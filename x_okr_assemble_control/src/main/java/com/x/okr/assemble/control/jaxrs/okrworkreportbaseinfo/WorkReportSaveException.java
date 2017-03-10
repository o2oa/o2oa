package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportSaveException( Throwable e ) {
		super("系统在保存工作汇报信息时发生异常.", e );
	}
}
