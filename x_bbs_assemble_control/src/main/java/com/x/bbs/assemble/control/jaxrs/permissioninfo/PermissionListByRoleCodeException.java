package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.exception.PromptException;

class PermissionListByRoleCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissionListByRoleCodeException( Throwable e, String code ) {
		super("系统在获取指定的角色Code绑定的所有权限的信息列表时发生异常.Role:" + code, e );
	}
}
