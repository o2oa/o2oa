package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;

import com.x.base.core.exception.PromptException;

class ReportDetailQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportDetailQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的工作汇报详细信息时发生异常。ID：" + id, e );
	}
}
