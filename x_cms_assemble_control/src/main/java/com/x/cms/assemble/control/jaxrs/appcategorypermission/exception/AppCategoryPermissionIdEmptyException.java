package com.x.cms.assemble.control.jaxrs.appcategorypermission.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryPermissionIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryPermissionIdEmptyException() {
		super("应用栏目分类权限配置信息ID为空，无法继续查询数据。" );
	}
}
