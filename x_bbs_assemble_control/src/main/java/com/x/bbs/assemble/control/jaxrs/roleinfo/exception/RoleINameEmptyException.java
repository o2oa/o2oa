package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class RoleINameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public RoleINameEmptyException() {
		super("角色名称为空，无法保存角色信息." );
	}
}
