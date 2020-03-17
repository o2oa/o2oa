package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionIdEmpty() {
		super("应用栏目分类管理员配置信息中“ID”不能为空。" );
	}
}
