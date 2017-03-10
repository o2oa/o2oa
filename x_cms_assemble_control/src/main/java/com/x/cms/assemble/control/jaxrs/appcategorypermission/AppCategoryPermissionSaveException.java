package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionSaveException( Throwable e ) {
		super("应用栏目分类权限配置信息保存时发生异常。", e );
	}
}
