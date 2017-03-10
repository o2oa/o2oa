package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoSaveException( Throwable e ) {
		super("系统在保存BBS角色信息时发生异常.", e );
	}
}
