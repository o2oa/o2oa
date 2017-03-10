package com.x.okr.assemble.control.servlet.reportattachment;

import com.x.base.core.exception.PromptException;

class WorkReportNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportNotExistsException( String id ) {
		super("指定ID的工作汇报信息记录不存在。ID：" + id );
	}
}
