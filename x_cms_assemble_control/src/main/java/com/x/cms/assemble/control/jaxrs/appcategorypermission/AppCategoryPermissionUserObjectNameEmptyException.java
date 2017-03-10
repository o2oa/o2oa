package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionUserObjectNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionUserObjectNameEmptyException() {
		super("应用栏目分类权限配置信息用户载体对象名称UserObjectName为空，无法继续查询或者保存数据。" );
	}
}
