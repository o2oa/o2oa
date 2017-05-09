package com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryAdminCategoryIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryAdminCategoryIdEmptyException() {
		super("应用栏目分类管理员配置信息分类ID为空，无法继续查询数据。" );
	}
}
