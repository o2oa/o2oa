package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportDeleteException( Throwable e, String id ) {
		super("删除指定ID的工作汇报信息时发生异常。ID：" + id, e );
	}
}
