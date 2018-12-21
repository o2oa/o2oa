package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

class WorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkNotExistsException( String id ) {
		super("指定ID的工作记录不存在。ID：" + id );
	}
}
