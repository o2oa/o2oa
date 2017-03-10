package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionListByIdsException( Throwable e ) {
		super("根据指定的ID列表查询应用栏目分类管理员配置信息时发生异常。", e );
	}
}
