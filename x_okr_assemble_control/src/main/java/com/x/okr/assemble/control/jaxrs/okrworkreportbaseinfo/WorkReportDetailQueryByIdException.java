package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportDetailQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportDetailQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的工作汇报详细信息时发生异常。ID：" + id, e );
	}
}
