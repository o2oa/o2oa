package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoBindException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoBindException( Throwable e ) {
		super("系统在根据人员姓名以及角色编码列表进行角色绑定时发生异常.", e );
	}
}
