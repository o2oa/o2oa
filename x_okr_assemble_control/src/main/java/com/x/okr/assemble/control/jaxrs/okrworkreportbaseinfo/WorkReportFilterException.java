package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportFilterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportFilterException( Throwable e ) {
		super("系统根据条件查询工作汇报信息列表时发生异常." , e );
	}
}
