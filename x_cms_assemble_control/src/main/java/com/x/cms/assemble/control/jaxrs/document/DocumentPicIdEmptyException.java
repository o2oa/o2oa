package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentPicIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentPicIdEmptyException() {
		super("文档信息ID为空，无法进行查询操作。" );
	}
}
