package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoNotExistsException( String id ) {
		super("指定ID的BBS角色不存在.ID:" + id );
	}
}
