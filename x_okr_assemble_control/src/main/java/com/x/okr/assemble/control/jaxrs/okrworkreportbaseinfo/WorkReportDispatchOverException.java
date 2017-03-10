package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportDispatchOverException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportDispatchOverException( Throwable e, String id ) {
		super("将指定ID的工作汇报信息调度至结束时发生异常。ID：" + id, e );
	}
}
