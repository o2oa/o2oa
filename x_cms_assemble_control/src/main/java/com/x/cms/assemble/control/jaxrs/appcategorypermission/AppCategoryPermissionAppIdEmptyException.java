package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionAppIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionAppIdEmptyException() {
		super("应用栏目分类权限配置信息应用栏目ID为空，无法继续查询或者保存数据。" );
	}
}
