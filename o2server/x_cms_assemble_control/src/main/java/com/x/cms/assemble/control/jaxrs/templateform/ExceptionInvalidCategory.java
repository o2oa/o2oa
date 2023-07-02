package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCategory extends LanguagePromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionInvalidCategory(String category) {
		super("分类字段校验不通过:{}.", category);
	}

}
