package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;

import com.x.base.core.exception.PromptException;

class ReportPersonLinkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportPersonLinkNotExistsException( String id ) {
		super("指定ID的工作汇报处理人信息记录不存在。ID：" + id );
	}
}
