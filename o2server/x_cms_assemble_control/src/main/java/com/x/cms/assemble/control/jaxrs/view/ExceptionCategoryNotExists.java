package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryNotExists( String id ) {
		super( "指定的分类不存在：{}." + id );
	}
}
