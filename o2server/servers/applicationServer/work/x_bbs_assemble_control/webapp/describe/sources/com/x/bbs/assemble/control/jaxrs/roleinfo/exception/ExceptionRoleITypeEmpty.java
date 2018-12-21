package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleITypeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleITypeEmpty() {
		super("角色类别为空，无法保存角色信息" );
	}
}
