package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNotExists( String id ) {
		super("指定的应用栏目不存在：{}" + id );
	}
}
