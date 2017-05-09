package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.exception.PromptException;

public class RoleCodeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public RoleCodeEmptyException() {
		super("角色编码roleCode为空， 无法进行查询." );
	}
}
