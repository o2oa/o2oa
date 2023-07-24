package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExists( String docId ) {
		super("指定的文档不存在:{}." + docId );
	}
}
