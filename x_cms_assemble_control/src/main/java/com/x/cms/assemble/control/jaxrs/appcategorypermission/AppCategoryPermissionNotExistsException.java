package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionNotExistsException( String id ) {
		super("指定ID的应用栏目分类权限配置信息不存在。ID:" + id );
	}
}
