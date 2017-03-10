package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentPicBase64EmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentPicBase64EmptyException() {
		super("文档大图信息为空，无法进行数据操作。" );
	}
}
