package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryIdEmpty() {
		super("分类信息“ID”不能为空。" );
	}
}
