package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionQueryByIdException( Throwable e, String id ) {
		super("根据ID查询应用栏目分类权限配置信息时发生异常。ID:" + id, e );
	}
}
