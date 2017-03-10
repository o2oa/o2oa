package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class PermissionListByRoleCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissionListByRoleCodeException( Throwable e, String code ) {
		super("系统在根据角色编码查询权限信息列表时发生异常！Code:" + code, e );
	}
}
