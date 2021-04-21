package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFormForEditNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFormForEditNotExists( String id ) {
		super("分类设置的文档编辑表单不存在。ID:{}", id );
	}
}
