package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryIdEmpty() {
		super("应用栏目分类管理员配置信息中“分类ID”不能为空。" );
	}
}
