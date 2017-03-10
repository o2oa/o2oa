package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionUserObjectTypeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionUserObjectTypeEmptyException() {
		super("应用栏目分类权限配置信息用户类别UserObjectType为空，无法继续查询或者保存数据。" );
	}
}
