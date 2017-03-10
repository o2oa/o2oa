package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

import com.x.base.core.exception.PromptException;

class ReportProcessLogNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportProcessLogNotExistsException( String id ) {
		super("指定ID的工作汇报处理人信息记录不存在。ID：" + id );
	}
}
