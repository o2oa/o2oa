package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionIdEmpty() {
		super("分类信息“ID”不能为空." );
	}
}
