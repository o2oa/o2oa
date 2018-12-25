package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleINameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleINameEmpty() {
		super("角色名称为空，无法保存角色信息." );
	}
}
