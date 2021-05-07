package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExists( String id ) {
		super("文档信息不存在，无法继续进行操作。id:{}", id );
	}
}
