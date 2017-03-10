package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.exception.PromptException;

class PermissionListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissionListAllException( Throwable e ) {
		super("列示所有的权限信息时时发生异常.", e );
	}
}
