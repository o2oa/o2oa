package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoPermissionSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoPermissionSave( Throwable e, String id ) {
		super( "保存分类权限时发生异常。ID:{}" ,id, e );
	}
}
