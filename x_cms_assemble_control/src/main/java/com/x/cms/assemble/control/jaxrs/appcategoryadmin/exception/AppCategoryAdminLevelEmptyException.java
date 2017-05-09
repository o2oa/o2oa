package com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryAdminLevelEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryAdminLevelEmptyException() {
		super("应用栏目分类管理员配置信息中管理员级别为空，无法继续保存数据。" );
	}
}
