package com.x.cms.assemble.control.jaxrs.appcategorypermission.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryPermissionUserObjectTypeInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryPermissionUserObjectTypeInvalidException( String type ) {
		super("应用栏目分类权限配置信息用户类别不合法, UserObjectType:" + type );
	}
}
