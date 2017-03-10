package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID获取BBS角色信息时发生异常！ID:" + id, e );
	}
}
