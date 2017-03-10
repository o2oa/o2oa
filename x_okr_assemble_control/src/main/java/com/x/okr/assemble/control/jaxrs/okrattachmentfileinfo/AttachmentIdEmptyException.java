package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import com.x.base.core.exception.PromptException;

class AttachmentIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentIdEmptyException() {
		super("id为空，无法继续进行查询操作。");
	}
}
