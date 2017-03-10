package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminObjectIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminObjectIdEmptyException() {
		super("应用栏目分类管理员配置信息对象ID为空，无法继续保存数据。" );
	}
}
