package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentTitleEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentTitleEmpty() {
		super("文档标题为空，无法创建文档信息。" );
	}
}
