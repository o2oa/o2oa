package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;

import com.x.base.core.exception.PromptException;

class ReportDetailDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportDetailDeleteException( Throwable e, String id ) {
		super("删除指定ID的工作汇报详细信息时发生异常。ID：" + id, e );
	}
}
