package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleITypeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleITypeEmptyException() {
		super("角色类别为空，无法保存角色信息" );
	}
}
