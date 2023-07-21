package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionPersonNameEmpty() {
		super("文档编辑表单信息ID为空，无法进行查询操作。" );
	}
}
