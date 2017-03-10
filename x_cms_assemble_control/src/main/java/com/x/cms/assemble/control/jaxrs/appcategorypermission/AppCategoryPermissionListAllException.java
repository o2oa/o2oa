package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionListAllException( Throwable e ) {
		super("查询所有应用栏目分类权限配置信息时发生异常。", e );
	}
}
