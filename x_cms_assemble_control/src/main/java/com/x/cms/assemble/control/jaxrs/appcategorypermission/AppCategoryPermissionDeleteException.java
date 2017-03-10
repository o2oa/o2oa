package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionDeleteException( Throwable e, String id ) {
		super("根据ID删除应用栏目分类权限配置信息时发生异常。ID:" + id, e );
	}
}
