package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppIdEmpty() {
		super("应用栏目分类管理员配置信息中“应用栏目ID”不能为空。" );
	}
}
