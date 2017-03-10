package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class InsufficientPermissionsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InsufficientPermissionsException( String name, String role ) {
		super("操作权限不足。Name:" + name + ", Role:" + role );
	}
}
