package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionImportViewIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionImportViewIdEmpty() {
		super("分类信息未绑定导入的列表，无法进行导入操作。" );
	}
}
