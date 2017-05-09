package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.exception.PromptException;

public class InsufficientPermissionsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public InsufficientPermissionsException( String name, String role ) {
		super("操作权限不足。Name:" + name + ", Role:" + role );
	}
}
