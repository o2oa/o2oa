package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminObjectTypeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminObjectTypeEmptyException() {
		super("应用栏目分类管理员配置信息对象类别ObjectType为空，无法继续查询或者保存数据。" );
	}
}
