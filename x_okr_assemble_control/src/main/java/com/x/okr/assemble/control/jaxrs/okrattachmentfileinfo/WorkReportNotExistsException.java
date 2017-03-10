package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import com.x.base.core.exception.PromptException;

class WorkReportNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportNotExistsException( String id ) {
		super("指定id的工作汇报信息不存在，无法继续进行操作。ID:" + id );
	}
}
