package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleINameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleINameEmptyException() {
		super("角色名称为空，无法保存角色信息." );
	}
}
