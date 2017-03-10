package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoDeleteException( Throwable e, String id ) {
		super("根据ID删除BBS角色信息时发生异常.ID:" + id, e );
	}
}
