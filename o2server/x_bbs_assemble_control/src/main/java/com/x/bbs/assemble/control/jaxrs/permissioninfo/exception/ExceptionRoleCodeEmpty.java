package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleCodeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleCodeEmpty() {
		super("角色编码roleCode为空， 无法进行查询." );
	}
}
