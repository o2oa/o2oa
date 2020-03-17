package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionAdminNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAdminNameEmpty() {
		super("应用栏目分类管理员配置信息中“管理员姓名”不能为空。" );
	}
}
