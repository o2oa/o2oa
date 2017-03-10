package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleCodeAutoCreateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleCodeAutoCreateException( Throwable e, String name ) {
		super("系统根据角色名称组织角色编码时发生异常.Name:" + name, e );
	}
}
