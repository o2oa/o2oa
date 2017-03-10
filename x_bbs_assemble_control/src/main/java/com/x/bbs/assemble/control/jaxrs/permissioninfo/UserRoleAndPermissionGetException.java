package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.exception.PromptException;

class UserRoleAndPermissionGetException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UserRoleAndPermissionGetException( Throwable e, String name ) {
		super("获取用户的论坛访问权限列表时发生异常.Person:" + name, e );
	}
}
