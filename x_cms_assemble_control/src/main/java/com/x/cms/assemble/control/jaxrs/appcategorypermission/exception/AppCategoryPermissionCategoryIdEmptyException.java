package com.x.cms.assemble.control.jaxrs.appcategorypermission.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryPermissionCategoryIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryPermissionCategoryIdEmptyException() {
		super("应用栏目分类权限配置信息应用分类ID为空，无法继续查询或者保存数据。" );
	}
}
