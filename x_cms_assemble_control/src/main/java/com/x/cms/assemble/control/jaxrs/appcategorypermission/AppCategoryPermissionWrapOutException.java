package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionWrapOutException( Throwable e ) {
		super("系统将查询出来的应用栏目分类权限信息转换为输出格式时发生异常。", e );
	}
}
