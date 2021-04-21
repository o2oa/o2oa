package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoAccessDenied extends LanguagePromptException {
	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoAccessDenied() {
		super("分类信息不允许匿名访问.");
	}
}
