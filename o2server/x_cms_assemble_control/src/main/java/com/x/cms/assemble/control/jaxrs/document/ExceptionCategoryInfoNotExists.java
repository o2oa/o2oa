package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoNotExists( String id ) {
		super("指定的分类信息不存在。ID:{}", id );
	}
}
