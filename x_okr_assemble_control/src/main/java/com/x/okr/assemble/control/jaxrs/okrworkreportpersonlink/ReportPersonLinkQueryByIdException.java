package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;

import com.x.base.core.exception.PromptException;

class ReportPersonLinkQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportPersonLinkQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的工作汇报处理人信息时发生异常。ID：" + id, e );
	}
}
