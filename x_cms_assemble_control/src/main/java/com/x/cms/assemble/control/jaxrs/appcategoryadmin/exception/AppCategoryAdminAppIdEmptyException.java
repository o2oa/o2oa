package com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryAdminAppIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryAdminAppIdEmptyException() {
		super("应用栏目分类管理员配置信息应用栏目ID为空，无法继续查询数据。" );
	}
}
